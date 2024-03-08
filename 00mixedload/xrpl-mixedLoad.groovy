@GrabConfig(systemClassLoader = true)
@Grab('net.simonix.scripts:groovy-jmeter')

@groovy.transform.BaseScript net.simonix.dsl.jmeter.TestScript script

import load_settings

start {

  // develop stage, 'dev', 'alpha', ... or 'prod'
  def stage  =  System.env['TEST_STAGE'] ?: 'dev'
  def notProd = stage != 'prod'

  plan {
    variables {
      // using __env() custom JMeter Functions, https://jmeter-plugins.org/wiki/Functions/
      variable(name: 'c_app_host_name', value: '${__env(c_app_host_name, , s1.ripple.com)}', description: 'Test server host name')
      variable(name: 'c_app_host_port', value: '${__env(c_app_host_port, , 51234)}', description: 'Test server host port')
      variable(name: 'c_app_protocol', value: '${__env(c_app_protocol, , http)}', description: 'Test server protocol')
      variable(name: 'c_app_error_kw', value: '${__P(c_app_error_kw,Wrong)}', description: 'keyword indicates wrong application returns')
      variable(name: 'c_lt_users', value: '${__P(c_lt_users, 10)}', description: 'loadtest users')
      variable(name: 'c_lt_ramp', value: '${__P(c_lt_ramp, 30)}', description: 'loadtest ramp up in seconds')
      variable(name: 'c_lt_loops', value: '${__P(c_lt_loops, 1)}', description: 'loadtest loops')
      variable(name: 'c_lt_duration', value: '${__P(c_lt_duration, 120)}', description: 'loadtest duration in seconds')
      variable(name: 'c_lt_delay', value: '${__P(c_lt_delay, 1)}', description: 'thread delay in seconds')
      variable(name: 'c_tt_range', value: '${__P(c_tt_range, 2000)}', description: 'Think Time: Maximum random number of ms to delay')
      variable(name: 'c_tt_delay', value: '${__P(c_tt_delay, 500)}', description: 'Think Time: Ms to delay in addition to random time')
      variable(name: 'c_pt_range', value: '${__P(c_pt_range, 12000)}', description: 'Pace Time: Maximum random number of ms to delay')
      variable(name: 'c_pt_delay', value: '${__P(c_pt_delay, 3000)}', description: 'Pace Time: Ms to delay in addition to random time')
      variable(name: 'c_cfg_TestName', value: 'server_info', description: 'Test name to identify different tests')
      variable(name: 'c_cfg_Influxdb', value: '${__env(c_cfg_Influxdb, , localhost)}', description: 'Influxdb server host name')
      variable(name: 'p_session_email', value: 'john')
      variable(name: 'p_session_password', value: 'john')
      }

    csv name: 'CSV accounts', file: '../common/accounts.csv', variables: ['s_account']
    csv name: 'CSV nft_ids', file: '../common/nft_ids.csv', variables: ['s_nft_id']

    // common file-beg configuration
    insert 'common/stationary-beg.groovy'

    debug '---- Thread Groups starts ----', enabled: false
    group(name: 'Thread Group server_info', delay: load_settings.v.server_info.delay, delayedStart: true,
      users: load_settings.v.server_info.users, rampUp: load_settings.v.server_info.ramp, keepUser: false,
      duration: load_settings.v.server_info.duration, loops: load_settings.v.server_info.loops,
      scheduler: load_settings.v.server_info.scheduler, enabled: load_settings.v.server_info.enabled) {

      debug '--== Tx: server_info ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 server_info', generate: true) {
        
        http (method: 'POST', path: '/', name: 'Tx01r server_info') {
          body '{"method":"server_info"}'
          //extract_jmes expression: 'book.id', variable: 'p_bookId'
        }
    
      }

      flow (name: 'Think Time Flow Control', enabled: false) {
        uniform_timer (name: 'Think Time', delay: '${c_tt_delay}', range: '${c_tt_range}')
      }

      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: '${c_tt_delay}', range: '${c_tt_range}')
      }
      // end group
    }

    group(name: 'Thread Group account_info', delay: load_settings.v.account_info.delay, delayedStart: true,
      users: load_settings.v.account_info.users, rampUp: load_settings.v.account_info.ramp, keepUser: false,
      duration: load_settings.v.account_info.duration, loops: load_settings.v.account_info.loops,
      scheduler: load_settings.v.account_info.scheduler, enabled: load_settings.v.account_info.enabled) {

      debug '--== Tx: account_info ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 account_info', generate: true) {

        http (method: 'POST', path: '/', name: 'Tx01r account_info') {
          body '''{"method":"account_info","params": [{"account":"${s_account}","ledger_index":"validated","queue":false}]}'''
        }

      }
      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: '${c_tt_delay}', range: '${c_tt_range}')
      }
    }

    group(name: 'Thread Group nft_info', delay: load_settings.v.nft_info.delay, delayedStart: true,
      users: load_settings.v.nft_info.users, rampUp: load_settings.v.nft_info.ramp, keepUser: false,
      duration: load_settings.v.nft_info.duration, loops: load_settings.v.nft_info.loops,
      scheduler: load_settings.v.nft_info.scheduler, enabled: load_settings.v.nft_info.enabled) {

      debug '--== Tx: nft_info ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 nft_info', generate: true) {

        http (method: 'POST', path: '/', name: 'Tx01r nft_info') {
          body '''{"method":"nft_info","params": [{"nft_id":"${s_nft_id}"}]}'''
        }

      }

      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: '${c_tt_delay}', range: '${c_tt_range}')
      }
   }

    group(name: 'Thread Group ledger_data', delay: load_settings.v.ledger_data.delay, delayedStart: true,
      users: load_settings.v.ledger_data.users, rampUp: load_settings.v.ledger_data.ramp, keepUser: false,
      duration: load_settings.v.ledger_data.duration, loops: load_settings.v.ledger_data.loops,
      scheduler: load_settings.v.ledger_data.scheduler, enabled: load_settings.v.ledger_data.enabled) {

      // cannot use "../"
      insert 'Ledger_data/ledger_data_ins.groovy'
    }

  // common file-end configuration
  insert 'common/stationary-end.groovy'
  }
}
