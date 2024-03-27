@GrabConfig(systemClassLoader = true)
@Grab('net.simonix.scripts:groovy-jmeter')

@groovy.transform.BaseScript net.simonix.dsl.jmeter.TestScript script

import groovy.yaml.*
def slurper = new YamlSlurper()

start {
  def load_testname = 'Tx_AccountDelete'
  def load_settings = slurper.parse("load_settings.yaml" as File)
  def load_setting = load_settings.settings[load_testname]

  plan {
    variables {
      // using __env() custom JMeter Functions, https://jmeter-plugins.org/wiki/Functions/
      variable(name: 'c_app_host_name', value: '${__env(c_app_host_name, , s1.ripple.com)}', description: 'Test server host name')
      variable(name: 'c_app_host_port', value: '${__env(c_app_host_port, , 51234)}', description: 'Test server host port')
      variable(name: 'c_app_protocol', value: '${__env(c_app_protocol, , http)}', description: 'Test server protocol')
      variable(name: 'c_app_error_kw', value: '${__P(c_app_error_kw,Wrong)}', description: 'keyword indicates wrong application returns')
      variable(name: 'c_lt_users', value: '${__P(c_lt_users, ' + load_setting.users + ')}', description: 'loadtest users')
      variable(name: 'c_lt_ramp', value: '${__P(c_lt_ramp, ' + load_setting.ramp + ')}', description: 'loadtest ramp up in seconds')
      variable(name: 'c_lt_loops', value: '${__P(c_lt_loops, ' + load_setting.loops + ')}', description: 'loadtest loops')
      variable(name: 'c_lt_duration', value: '${__P(c_lt_duration, ' + load_setting.duration + ')}', description: 'loadtest duration in seconds')
      variable(name: 'c_lt_delay', value: '${__P(c_lt_delay, ' + load_setting.delay + ')}', description: 'thread delay in seconds')
      variable(name: 'c_tt_range', value: '${__P(c_tt_range, 2000)}', description: 'Think Time: Maximum random number of ms to delay')
      variable(name: 'c_tt_delay', value: '${__P(c_tt_delay, 500)}', description: 'Think Time: Ms to delay in addition to random time')
      variable(name: 'c_pt_range', value: '${__P(c_pt_range, 12000)}', description: 'Pace Time: Maximum random number of ms to delay')
      variable(name: 'c_pt_delay', value: '${__P(c_pt_delay, 3000)}', description: 'Pace Time: Ms to delay in addition to random time')
      variable(name: 'c_cfg_TestName', value: 'Tx_AccountDelete', description: 'Test name to identify different tests')
      variable(name: 'c_cfg_Influxdb', value: '${__env(c_cfg_Influxdb, , localhost)}', description: 'Influxdb server host name')
      variable(name: 'p_session_email', value: 'john')
      variable(name: 'p_session_password', value: 'john')
      }

    csv name: 'CSV Tx_AccountDelete', file: '../common/Tx_AccountDelete.csv', variables: ["acct","vCurrency","vIssuer","ledgerHash","ledgerIndex"]

    // common file-beg configuration
    insert 'common/stationary-beg.gvy'

    check_response applyTo: 'parent', {
      text() excludes ',\\"status\\":\\"error\\",\\"type\\":\\"response\\"'
    }

    debug '---- Thread Groups starts ----', enabled: false
    group(name: 'TGroup', delay: '${c_lt_delay}', delayedStart: true,
      users: '${c_lt_users}', rampUp: '${c_lt_ramp}', keepUser: false,
      loops: '${c_lt_loops}', duration: '${c_lt_duration}', scheduler: true) {

      insert 'Tx_AccountDelete_ins.gvy'
    }

  // common file-end configuration
  insert 'common/stationary-end.gvy'
  }
}
