@GrabConfig(systemClassLoader = true)
@Grab('net.simonix.scripts:groovy-jmeter')

@groovy.transform.BaseScript net.simonix.dsl.jmeter.TestScript script

import groovy.yaml.*
def slurper = new YamlSlurper()

start {
  def load_testname = 'Tx_TicketCreate'
  def load_settings = slurper.parse("load_settings.yaml" as File)
  def load_setting = load_settings.settings

  plan {
    variables {
      // using __env() custom JMeter Functions, https://jmeter-plugins.org/wiki/Functions/
      variable(name: 'c_app_host_name', value: '${__env(c_app_host_name, , 172.16.0.167)}', description: 'Test server host name')
      variable(name: 'c_app_host_port', value: '${__env(c_app_host_port, , 51234)}', description: 'Test server host port')
      variable(name: 'c_app_protocol', value: '${__env(c_app_protocol, , http)}', description: 'Test server protocol')
      variable(name: 'c_app_error_kw', value: '${__P(c_app_error_kw,Wrong)}', description: 'keyword indicates wrong application returns')
      variable(name: 'c_lt_users', value: '${__P(c_lt_users, ' + load_setting[load_testname].users + ')}', description: 'loadtest users')
      variable(name: 'c_lt_ramp', value: '${__P(c_lt_ramp, ' + load_setting[load_testname].ramp + ')}', description: 'loadtest ramp up in seconds')
      variable(name: 'c_lt_loops', value: '${__P(c_lt_loops, ' + load_setting[load_testname].loops + ')}', description: 'loadtest loops')
      variable(name: 'c_lt_duration', value: '${__P(c_lt_duration, ' + load_setting[load_testname].duration + ')}', description: 'loadtest duration in seconds')
      variable(name: 'c_lt_delay', value: '${__P(c_lt_delay, ' + load_setting[load_testname].delay + ')}', description: 'thread delay in seconds')
      variable(name: 'c_tt_range', value: '${__P(c_tt_range, 200)}', description: 'Think Time: Maximum random number of ms to delay')
      variable(name: 'c_tt_delay', value: '${__P(c_tt_delay, 50)}', description: 'Think Time: Ms to delay in addition to random time')
      variable(name: 'c_pt_range', value: '${__P(c_pt_range, 1000)}', description: 'Pace Time: Maximum random number of ms to delay')
      variable(name: 'c_pt_delay', value: '${__P(c_pt_delay, 30)}', description: 'Pace Time: Ms to delay in addition to random time')
      variable(name: 'c_cfg_TestName', value: 'Tx_TicketCreate', description: 'Test name to identify different tests')
      variable(name: 'c_cfg_Influxdb', value: '${__env(c_cfg_Influxdb, , localhost)}', description: 'Influxdb server host name')
      variable(name: 'p_acct_dest', value: 'rMQ98K56yXJbDGv49ZSmW51sLn94Xe1mu1', description: 'Destination address to receive any leftover XRP after deleting')
      variable(name: 'p_del_fee', value: '${__P(p_del_fee, 20)}', description: 'Fee of deleting account (2000000)')
      }

    csv name: 'CSV Tx_TicketCreate', file: 'Tx_TicketCreate.csv', variables: ["s_account","s_secret"]

    // common file-beg configuration
    insert 'common/stationary-beg.gvy'

    check_response applyTo: 'children', {
      text() includes '"status":"success"'
    }
    check_response applyTo: 'children', {
      text() excludes ',\\"status\\":\\"error\\",\\"type\\":\\"response\\"'
    }
    check_response applyTo: 'children', {
      text() excludes ',\\"error_code\\":'
    }

    debug '---- Thread Groups starts ----', enabled: false
    insert 'Tx_TicketCreate_ins.gvy', variables: [
      "vf_name": 'TGroup-Tx_TicketCreate', "vf_enabled": true, "vf_delay": '${c_lt_delay}',
      "vf_users": '${c_lt_users}', "vf_rampUp": '${c_lt_ramp}',
      "vf_loops": '${c_lt_loops}', "vf_duration": '${c_lt_duration}',
      "vf_pt_delay": '${c_pt_delay}',  "vf_pt_range": '${c_pt_range}']

  // common file-end configuration
  insert 'common/stationary-end.gvy'
  }
}
