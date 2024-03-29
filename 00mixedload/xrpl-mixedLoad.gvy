@GrabConfig(systemClassLoader = true)
@Grab('net.simonix.scripts:groovy-jmeter')

@groovy.transform.BaseScript net.simonix.dsl.jmeter.TestScript script

import groovy.yaml.*
def slurper = new YamlSlurper()

start {

  // develop stage, 'dev', 'alpha', ... or 'prod'
  def stage  =  System.env['TEST_STAGE'] ?: 'dev'
  def notProd = stage != 'prod'
  def load_settings = slurper.parse("load_settings.yaml" as File)
  def load_setting = load_settings.settings

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
    insert 'common/stationary-beg.gvy'

    debug '---- Thread Groups starts ----', enabled: false
    group(name: 'TGroup-server_info', delay: load_setting["server_info"].delay, delayedStart: true,
      users: load_setting["server_info"].users, rampUp: load_setting["server_info"].ramp, keepUser: false,
      duration: load_setting["server_info"].duration, loops: load_setting["server_info"].loops,
      scheduler: load_setting["server_info"].scheduler, enabled: load_setting["server_info"].enabled) {

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
        uniform_timer (name: 'Pace Time', delay: '${c_pt_delay}', range: '${c_tt_range}')
      }
      // end group
    }

    group(name: 'TGroup-account_info', delay: load_setting["account_info"].delay, delayedStart: true,
      users: load_setting["account_info"].users, rampUp: load_setting["account_info"].ramp, keepUser: false,
      duration: load_setting["account_info"].duration, loops: load_setting["account_info"].loops,
      scheduler: load_setting["account_info"].scheduler, enabled: load_setting["account_info"].enabled) {

      insert 'inc_ledger_validated/account_info.gvy'
    }

    group(name: 'TGroup-fee', delay: load_setting["fee"].delay, delayedStart: true,
      users: load_setting["fee"].users, rampUp: load_setting["fee"].ramp, keepUser: false,
      duration: load_setting["fee"].duration, loops: load_setting["fee"].loops,
      scheduler: load_setting["fee"].scheduler, enabled: load_setting["fee"].enabled) {
      insert 'inc_ledger_validated/fee.gvy'
    }

    insert 'inc_ledger_validated/ledger_current.gvy', variables:
     ["vf_name": 'TGroup-ledger_current', "vf_enabled": load_setting["ledger_current"].enabled, "vf_delay": load_setting["ledger_current"].delay,
      "vf_users": load_setting["ledger_current"].users, "vf_rampUp": load_setting["ledger_current"].ramp,
      "vf_duration": load_setting["ledger_current"].duration, "vf_loops": load_setting["ledger_current"].loops,
      "vf_pt_delay": load_setting["ledger_current"].pt_delay,  "vf_pt_range": load_setting["ledger_current"].pt_range]

    insert 'inc_ledger_validated/ledger_closed.gvy', variables:
     ["vf_name": 'TGroup-ledger_closed', "vf_enabled": load_setting["ledger_closed"].enabled, "vf_delay": load_setting["ledger_closed"].delay,
      "vf_users": load_setting["ledger_closed"].users, "vf_rampUp": load_setting["ledger_closed"].ramp,
      "vf_duration": load_setting["ledger_closed"].duration, "vf_loops": load_setting["ledger_closed"].loops,
      "vf_pt_delay": load_setting["ledger_closed"].pt_delay,  "vf_pt_range": load_setting["ledger_closed"].pt_range]

    group(name: 'TGroup-nft_info', delay: load_setting["nft_info"].delay, delayedStart: true,
      users: load_setting["nft_info"].users, rampUp: load_setting["nft_info"].ramp, keepUser: false,
      duration: load_setting["nft_info"].duration, loops: load_setting["nft_info"].loops,
      scheduler: load_setting["nft_info"].scheduler, enabled: load_setting["nft_info"].enabled) {

      insert 'inc_ledger_validated/nft_info.gvy'
   }

    group(name: 'TGroup-book_offers', delay: load_setting["book_offers"].delay, delayedStart: true,
      users: load_setting["book_offers"].users, rampUp: load_setting["book_offers"].ramp, keepUser: false,
      duration: load_setting["book_offers"].duration, loops: load_setting["book_offers"].loops,
      scheduler: load_setting["book_offers"].scheduler, enabled: load_setting["book_offers"].enabled) {

      insert 'book_offers/book_offers_ins.gvy'
    }

    insert 'ledger_data/ledger_data_ins.gvy', variables:
     ["vf_name": 'TGroup-ledger_data', "vf_enabled": load_setting["ledger_data"].enabled, "vf_delay": load_setting["ledger_data"].delay,
      "vf_users": load_setting["ledger_data"].users, "vf_rampUp": load_setting["ledger_data"].ramp,
      "vf_duration": load_setting["ledger_data"].duration, "vf_loops": load_setting["ledger_data"].loops,
      "vf_pt_delay": load_setting["ledger_data"].pt_delay,  "vf_pt_range": load_setting["ledger_data"].pt_range]

  // common file-end configuration
  insert 'common/stationary-end.gvy'
  }
}
