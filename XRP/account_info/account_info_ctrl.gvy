@GrabConfig(systemClassLoader = true)
@Grab('net.simonix.scripts:groovy-jmeter')

@groovy.transform.BaseScript net.simonix.dsl.jmeter.TestScript script

import groovy.yaml.*
def slurper = new YamlSlurper()

start {
  def load_testname = 'account_info'
  def load_settings = slurper.parse("load_settings.yaml" as File)
  def load_setting = load_settings.settings

  plan {
    variables {
      // using __env() custom JMeter Functions, https://jmeter-plugins.org/wiki/Functions/
      variable(name: 'c_app_host_name', value: '${__env(c_app_host_name, , 172.16.0.167)}', description: 'Test server host name')
      variable(name: 'c_app_host_port', value: '${__env(c_app_host_port, , 51233)}', description: 'Test server host port')
      variable(name: 'c_app_protocol', value: '${__env(c_app_protocol, , http)}', description: 'Test server protocol')
      variable(name: 'c_app_error_kw', value: '${__P(c_app_error_kw,Wrong)}', description: 'keyword indicates wrong application returns')
      // variable(name: 'c_lt_users', value: '${__P(c_lt_users, 10)}', description: 'loadtest users')
      // variable(name: 'c_lt_ramp', value: '${__P(c_lt_ramp, 30)}', description: 'loadtest ramp up in seconds')
      // variable(name: 'c_lt_loops', value: '${__P(c_lt_loops, 1)}', description: 'loadtest loops')
      // variable(name: 'c_lt_duration', value: '${__P(c_lt_duration, 120)}', description: 'loadtest duration in seconds')
      // variable(name: 'c_lt_delay', value: '${__P(c_lt_delay, 0)}', description: 'thread delay in seconds')
      variable(name: 'c_lt_users', value: '${__P(c_lt_users, ' + load_setting[load_testname].users + ')}', description: 'loadtest users')
      variable(name: 'c_lt_ramp', value: '${__P(c_lt_ramp, ' + load_setting[load_testname].ramp + ')}', description: 'loadtest ramp up in seconds')
      variable(name: 'c_lt_loops', value: '${__P(c_lt_loops, ' + load_setting[load_testname].loops + ')}', description: 'loadtest loops')
      variable(name: 'c_lt_duration', value: '${__P(c_lt_duration, ' + load_setting[load_testname].duration + ')}', description: 'loadtest duration in seconds')
      variable(name: 'c_lt_delay', value: '${__P(c_lt_delay, ' + load_setting[load_testname].delay + ')}', description: 'thread delay in seconds')
      variable(name: 'c_tt_range', value: '${__P(c_tt_range, 200)}', description: 'Think Time: Maximum random number of ms to delay')
      variable(name: 'c_tt_delay', value: '${__P(c_tt_delay, 50)}', description: 'Think Time: Ms to delay in addition to random time')
      variable(name: 'c_pt_range', value: '${__P(c_pt_range, 1000)}', description: 'Pace Time: Maximum random number of ms to delay')
      variable(name: 'c_pt_delay', value: '${__P(c_pt_delay, 30)}', description: 'Pace Time: Ms to delay in addition to random time')
      variable(name: 'c_cfg_TestName', value: 'account_info', description: 'Test name to identify different tests')
      variable(name: 'c_cfg_Influxdb', value: '${__env(c_cfg_Influxdb, , localhost)}', description: 'Influxdb server host name')
      variable(name: 'c_app_kw_expect', value: '${__P(c_app_kw_expect,tesSUCCESS)}', description: 'application keyword expected')
      variable(name: 'p_ledger_type', value: '${__P(p_ledger_type, validated)}')
      }

    csv name: 'CSV account_info', file: 'account_info.csv', variables: ["s_account","s_secret"], recycle: false, stopUser: true
    csv name: 'CSV Hosts', file: 'servers.csv', variables: ["s_hlabel","c_app_host_name"], shareMode: "group"

    // common file-beg configuration
    insert 'common/stationary-beg.gvy'

    // check_response {
    //   text() includes ',"status":"success",'
    // }
   check_response {
      text() excludes ',"error_code":'
    }

    debug '---- Thread Groups starts ----', enabled: false
    insert 'account_info_ins.gvy', variables:
     ["vf_name": 'TGroup-account_info', "vf_suffix": 'V', "vf_enabled": load_setting["account_info"].enabled, "vf_delay": load_setting["account_info"].delay,
      // "vf_users": load_setting["account_info"].users, "vf_loops": -1,
      "vf_users": '${c_lt_users}', "vf_loops": -1,
      "vf_duration": '${c_lt_duration}', "vf_rampUp": '${c_lt_ramp}', "vf_ledger_type": "p_ledger_type",
      "vf_pt_delay": load_setting["account_info"].pt_delay,  "vf_pt_range": load_setting["account_info"].pt_range]

  // common file-end configuration
  insert 'common/stationary-end.gvy'
  }
}
