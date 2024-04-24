@GrabConfig(systemClassLoader = true)
@Grab('net.simonix.scripts:groovy-jmeter')

@groovy.transform.BaseScript net.simonix.dsl.jmeter.TestScript script

import groovy.yaml.*
def slurper = new YamlSlurper()

start {
  def load_testname = 'account_info'
  def load_settings = slurper.parse("load_settings.yaml" as File)
  def load_setting = load_settings.settings[load_testname]

  plan {
    variables {
      // using __env() custom JMeter Functions, https://jmeter-plugins.org/wiki/Functions/
      variable(name: 'c_app_host_name', value: '${__env(c_app_host_name, , 172.16.0.167)}', description: 'Test server host name')
      variable(name: 'c_app_host_port', value: '${__env(c_app_host_port, , 51234)}', description: 'Test server host port')
      variable(name: 'c_app_protocol', value: '${__env(c_app_protocol, , http)}', description: 'Test server protocol')
      variable(name: 'c_app_error_kw', value: '${__P(c_app_error_kw,Wrong)}', description: 'keyword indicates wrong application returns')
      variable(name: 'c_lt_users', value: '${__P(c_lt_users, 10)}', description: 'loadtest users')
      variable(name: 'c_lt_ramp', value: '${__P(c_lt_ramp, 30)}', description: 'loadtest ramp up in seconds')
      variable(name: 'c_lt_loops', value: '${__P(c_lt_loops, 1)}', description: 'loadtest loops')
      variable(name: 'c_lt_duration', value: '${__P(c_lt_duration, 120)}', description: 'loadtest duration in seconds')
      variable(name: 'c_lt_delay', value: '${__P(c_lt_delay, 0)}', description: 'thread delay in seconds')
      variable(name: 'c_tt_range', value: '${__P(c_tt_range, 200)}', description: 'Think Time: Maximum random number of ms to delay')
      variable(name: 'c_tt_delay', value: '${__P(c_tt_delay, 50)}', description: 'Think Time: Ms to delay in addition to random time')
      variable(name: 'c_pt_range', value: '${__P(c_pt_range, 1000)}', description: 'Pace Time: Maximum random number of ms to delay')
      variable(name: 'c_pt_delay', value: '${__P(c_pt_delay, 30)}', description: 'Pace Time: Ms to delay in addition to random time')
      variable(name: 'c_cfg_TestName', value: 'account_info', description: 'Test name to identify different tests')
      variable(name: 'c_cfg_Influxdb', value: '${__env(c_cfg_Influxdb, , localhost)}', description: 'Influxdb server host name')
      variable(name: 'c_app_kw_expect', value: '${__P(c_app_kw_expect,tesSUCCESS)}', description: 'application keyword expected')
      }

    csv name: 'CSV account_info', file: 'account_info.csv', variables: ["s_account","s_secret"], recycle: false, stopUser: true
    csv name: 'CSV Hosts', file: '../../common/ch_servers.csv', variables: ["s_hlabel","c_app_host_name"], shareMode: "group"

    // common file-beg configuration
    insert 'common/stationary-beg.gvy'

    check_response applyTo: 'children', {
      text() includes ',"status":"success",'
    }
   check_response applyTo: 'children', {
      text() excludes ',"error_code":'
    }

    debug '---- Thread Groups starts ----', enabled: false
    group(name: 'TGroup', delay: '${c_lt_delay}', delayedStart: true,
      users: '${c_lt_users}', rampUp: '${c_lt_ramp}', keepUser: false,
      loops: -1, duration: '${c_lt_duration}', scheduler: true) {

      insert 'account_info_ins.gvy'
    }

  // common file-end configuration
  insert 'common/stationary-end.gvy'
  }
}
