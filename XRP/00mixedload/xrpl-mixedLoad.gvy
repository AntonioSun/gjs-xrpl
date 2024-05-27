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
      variable(name: 'c_app_host_name', value: '${__env(c_app_host_name, , 172.16.0.167)}', description: 'Test server host name')
      variable(name: 'c_app_host_port', value: '${__env(c_app_host_port, , 51233)}', description: 'Test server host port')
      variable(name: 'c_app_protocol', value: '${__env(c_app_protocol, , http)}', description: 'Test server protocol')
      variable(name: 'c_app_testdesc', value: '${__env(c_app_testdesc,,)}', description: 'Test server protocol')
      variable(name: 'c_app_error_kw', value: '${__P(c_app_error_kw,Wrong)}', description: 'keyword indicates wrong application returns')
      variable(name: 'c_lt_users', value: '${__P(c_lt_users, 10)}', description: 'loadtest users')
      variable(name: 'c_lt_ramp', value: '${__P(c_lt_ramp, 30)}', description: 'loadtest ramp up in seconds')
      variable(name: 'c_lt_loops', value: '${__P(c_lt_loops, 1)}', description: 'loadtest loops')
      variable(name: 'c_lt_duration', value: '${__P(c_lt_duration, 120)}', description: 'loadtest duration in seconds')
      variable(name: 'c_lt_delay', value: '${__P(c_lt_delay, 1)}', description: 'thread delay in seconds')
      variable(name: 'c_tt_range', value: '${__P(c_tt_range, 200)}', description: 'Think Time: Maximum random number of ms to delay')
      variable(name: 'c_tt_delay', value: '${__P(c_tt_delay, 50)}', description: 'Think Time: Ms to delay in addition to random time')
      variable(name: 'c_pt_range', value: '${__P(c_pt_range, 1000)}', description: 'Pace Time: Maximum random number of ms to delay')
      variable(name: 'c_pt_delay', value: '${__P(c_pt_delay, 30)}', description: 'Pace Time: Ms to delay in addition to random time')
      variable(name: 'c_cfg_TestName', value: 'xrpl-mixedload', description: 'Test name to identify different tests')
      variable(name: 'c_cfg_Influxdb', value: '${__env(c_cfg_Influxdb, , localhost)}', description: 'Influxdb server host name')
      variable(name: 'p_ledger_typeC', value: 'current')
      variable(name: 'p_ledger_typeV', value: 'validated')
      }

    csv name: 'CSV hosts', file: 'servers.csv', variables: ["s_hlabel","c_app_host_name"], shareMode: "group"
    csv name: 'CSV accounts', file: 'accounts.csv', variables: ['s_account']

    // common file-beg configuration
    insert 'common/stationary-beg.gvy'

    check_response {
      text() excludes '"error":"(noCurrent|noNetwork)","error_code":'
    }
    check_response {
      text() excludes '"error":".*","error_code":'
    }
    // check_response {
    //   text() includes ',"status":"success",'
    // } // Not working for server_info

    debug '---- Thread Groups starts ----', enabled: false
    insert 'inc_forwarded/server_info.gvy', variables:
     ["vf_name": 'TGroup-server_info', "vf_enabled": load_setting["server_info"].enabled, "vf_delay": load_setting["server_info"].delay,
      "vf_users": load_setting["server_info"].users, "vf_loops": -1,
      "vf_duration": '${c_lt_duration}', "vf_rampUp": '${c_lt_ramp}',
      "vf_pt_delay": load_setting["server_info"].pt_delay,  "vf_pt_range": load_setting["server_info"].pt_range]

    insert 'inc_forwarded/fee.gvy', variables:
     ["vf_name": 'TGroup-fee', "vf_enabled": load_setting["fee"].enabled, "vf_delay": load_setting["fee"].delay,
      "vf_users": load_setting["fee"].users, "vf_loops": -1,
      "vf_duration": '${c_lt_duration}', "vf_rampUp": '${c_lt_ramp}',
      "vf_pt_delay": load_setting["fee"].pt_delay,  "vf_pt_range": load_setting["fee"].pt_range]

    insert 'inc_forwarded/ledger_current.gvy', variables:
     ["vf_name": 'TGroup-ledger_current', "vf_enabled": load_setting["ledger_current"].enabled, "vf_delay": load_setting["ledger_current"].delay,
      "vf_users": load_setting["ledger_current"].users, "vf_loops": -1,
      "vf_duration": '${c_lt_duration}', "vf_rampUp": '${c_lt_ramp}',
      "vf_pt_delay": load_setting["ledger_current"].pt_delay,  "vf_pt_range": load_setting["ledger_current"].pt_range]

    insert 'inc_forwarded/ledger_closed.gvy', variables:
     ["vf_name": 'TGroup-ledger_closed', "vf_enabled": load_setting["ledger_closed"].enabled, "vf_delay": load_setting["ledger_closed"].delay,
      "vf_users": load_setting["ledger_closed"].users, "vf_loops": -1,
      "vf_duration": '${c_lt_duration}', "vf_rampUp": '${c_lt_ramp}',
      "vf_pt_delay": load_setting["ledger_closed"].pt_delay,  "vf_pt_range": load_setting["ledger_closed"].pt_range]

    // -
    // == validated ledger requests
    // -
    debug '... validated ledger requests ...', enabled: false
    insert 'inc_ledger_validated/account_info.gvy', variables:
     ["vf_name": 'TGroup-account_info', "vf_suffix": 'V', "vf_enabled": load_setting["account_info"].enabled, "vf_delay": load_setting["account_info"].delay,
      "vf_users": load_setting["account_info"].users, "vf_loops": -1,
      "vf_duration": '${c_lt_duration}', "vf_rampUp": '${c_lt_ramp}', "vf_ledger_type": "p_ledger_typeV",
      "vf_pt_delay": load_setting["account_info"].pt_delay,  "vf_pt_range": load_setting["account_info"].pt_range]

    insert 'inc_ledger_validated/ledger.gvy', variables:
     ["vf_name": 'TGroup-ledger', "vf_suffix": 'V', "vf_enabled": load_setting["ledger"].enabled, "vf_delay": load_setting["ledger"].delay,
      "vf_users": load_setting["ledger"].users, "vf_loops": -1,
      "vf_duration": '${c_lt_duration}', "vf_rampUp": '${c_lt_ramp}', "vf_ledger_type": "p_ledger_typeV",
      "vf_pt_delay": load_setting["ledger"].pt_delay,  "vf_pt_range": load_setting["ledger"].pt_range]

    insert 'inc_ledger_validated/nft_info.gvy', variables:
     ["vf_name": 'TGroup-nft_info', "vf_suffix": 'V', "vf_enabled": load_setting["nft_info"].enabled, "vf_delay": load_setting["nft_info"].delay,
      "vf_users": load_setting["nft_info"].users, "vf_loops": -1,
      "vf_duration": '${c_lt_duration}', "vf_rampUp": '${c_lt_ramp}',
      "vf_pt_delay": load_setting["nft_info"].pt_delay,  "vf_pt_range": load_setting["nft_info"].pt_range]

    insert 'book_offers/book_offers_ins.gvy', variables:
     ["vf_name": 'TGroup-book_offers', "vf_suffix": 'V', "vf_enabled": load_setting["book_offers"].enabled, "vf_delay": load_setting["book_offers"].delay,
      "vf_users": load_setting["book_offers"].users, "vf_loops": -1,
      "vf_duration": '${c_lt_duration}', "vf_rampUp": '${c_lt_ramp}',
      "vf_pt_delay": load_setting["book_offers"].pt_delay,  "vf_pt_range": load_setting["book_offers"].pt_range]

    insert 'ledger_data/ledger_data_ins.gvy', variables:
     ["vf_name": 'TGroup-ledger_data', "vf_suffix": 'V', "vf_enabled": load_setting["ledger_data"].enabled, "vf_delay": load_setting["ledger_data"].delay,
      "vf_users": load_setting["ledger_data"].users, "vf_loops": -1,
      "vf_duration": '${c_lt_duration}', "vf_rampUp": '${c_lt_ramp}',
      "vf_pt_delay": load_setting["ledger_data"].pt_delay,  "vf_pt_range": load_setting["ledger_data"].pt_range]

    // -
    // == current ledger requests
    // -
    debug '... current ledger requests ...', enabled: false
    insert 'inc_ledger_validated/account_info.gvy', variables:
     ["vf_name": 'TGroup-account_info', "vf_suffix": 'C', "vf_enabled": load_setting["account_info"].enabled, "vf_delay": load_setting["account_info"].delay,
      "vf_users": load_setting["account_info"].users, "vf_loops": -1,
      "vf_duration": '${c_lt_duration}', "vf_rampUp": '${c_lt_ramp}', "vf_ledger_type": "p_ledger_typeC",
      "vf_pt_delay": load_setting["account_info"].pt_delay,  "vf_pt_range": load_setting["account_info"].pt_range]

    insert 'inc_ledger_validated/ledger.gvy', variables:
     ["vf_name": 'TGroup-ledger', "vf_suffix": 'C', "vf_enabled": load_setting["ledger"].enabled, "vf_delay": load_setting["ledger"].delay,
      "vf_users": load_setting["ledger"].users, "vf_loops": -1,
      "vf_duration": '${c_lt_duration}', "vf_rampUp": '${c_lt_ramp}', "vf_ledger_type": "p_ledger_typeC",
      "vf_pt_delay": load_setting["ledger"].pt_delay,  "vf_pt_range": load_setting["ledger"].pt_range]

    insert 'inc_ledger_current/server_state.gvy', variables:
     ["vf_name": 'TGroup-server_state', "vf_suffix": 'C', "vf_enabled": load_setting["server_state"].enabled, "vf_delay": load_setting["server_state"].delay,
      "vf_users": load_setting["server_state"].users, "vf_loops": -1,
      "vf_duration": '${c_lt_duration}', "vf_rampUp": '${c_lt_ramp}',
      "vf_pt_delay": load_setting["server_state"].pt_delay,  "vf_pt_range": load_setting["server_state"].pt_range]

    // common file-end configuration
    insert 'common/stationary-end.gvy'
  }
}
