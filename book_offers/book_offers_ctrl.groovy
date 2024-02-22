@GrabConfig(systemClassLoader = true)
@Grab('net.simonix.scripts:groovy-jmeter')

@groovy.transform.BaseScript net.simonix.dsl.jmeter.TestScript script

import load_settings

start {
  plan {
    variables {
      // using __env() custom JMeter Functions, https://jmeter-plugins.org/wiki/Functions/
      variable(name: 'c_app_host_name', value: '${__env(c_app_host_name, , s1.ripple.com)}', description: 'Test server host name')
      variable(name: 'c_app_host_port', value: '${__env(c_app_host_port, , 51234)}', description: 'Test server host port')
      variable(name: 'c_app_protocol', value: '${__env(c_app_protocol, , http)}', description: 'Test server protocol')
      variable(name: 'c_app_error_kw', value: '${__P(c_app_error_kw,Wrong)}', description: 'keyword indicates wrong application returns')
      variable(name: 'c_lt_users', value: '${__P(c_lt_users, ' + load_settings.v.book_offers.users + ')}', description: 'loadtest users')
      variable(name: 'c_lt_ramp', value: '${__P(c_lt_ramp, ' + load_settings.v.book_offers.ramp + ')}', description: 'loadtest ramp up in seconds')
      variable(name: 'c_lt_loops', value: '${__P(c_lt_loops, ' + load_settings.v.book_offers.loops + ')}', description: 'loadtest loops')
      variable(name: 'c_lt_duration', value: '${__P(c_lt_duration, ' + load_settings.v.book_offers.duration + ')}', description: 'loadtest duration in seconds')
      variable(name: 'c_lt_delay', value: '${__P(c_lt_delay, ' + load_settings.v.book_offers.delay + ')}', description: 'thread delay in seconds')
      variable(name: 'c_tt_range', value: '${__P(c_tt_range, 2000)}', description: 'Think Time: Maximum random number of ms to delay')
      variable(name: 'c_tt_delay', value: '${__P(c_tt_delay, 500)}', description: 'Think Time: Ms to delay in addition to random time')
      variable(name: 'c_pt_range', value: '${__P(c_pt_range, 12000)}', description: 'Pace Time: Maximum random number of ms to delay')
      variable(name: 'c_pt_delay', value: '${__P(c_pt_delay, 3000)}', description: 'Pace Time: Ms to delay in addition to random time')
      variable(name: 'c_cfg_TestName', value: 'book_offers', description: 'Test name to identify different tests')
      variable(name: 'c_cfg_TimeTag', value: '_${START.YMD}-${START.HMS}', description: 'Time based tag to identify different tests')
      variable(name: 'c_cfg_Influxdb', value: '${__env(c_cfg_Influxdb, , localhost)}', description: 'Influxdb server host name')
      variable(name: 'p_session_email', value: 'john')
      variable(name: 'p_session_password', value: 'john')
      }
    variables {
      variable(name: 'c_cfg_TestID', value: '${c_cfg_TestName}${c_cfg_TimeTag}', description: 'Test ID, for identification and reporting purpose')
    }

    csv name: 'CSV book_offers', file: '../common/book_offers.csv', variables: ["acct","vCurrency","vIssuer","ledgerHash","ledgerIndex"]

    debug '---- default request settings ----', enabled: false
    defaults(protocol: '${c_app_protocol}', domain: '${c_app_host_name}', port:  '${c_app_host_port}')
    headers {
      header(name: 'Host', value: '${c_app_host_name}')
      header(name: 'Origin', value: '${c_app_host_name}')
      header(name: 'Referer', value: '${c_app_host_name}')
      header(name: 'Connection', value: 'keep-alive')
      header(name: 'Cache-Control', value: 'max-age=0')
      header(name: 'Upgrade-Insecure-Requests', value: '1')
      header(name: 'User-Agent', value: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36')
    }
    cookies()
    cache()

    debug '---- default error checkings ----', enabled: false
    check_response {
      status() eq 200 or 302
    }
    check_response applyTo: 'parent', {
      text() excludes '${c_app_error_kw}'
    }

    debug '---- Thread Groups starts ----', enabled: false
    group(name: 'Thread Group', delay: '${c_lt_delay}', delayedStart: true,
      users: '${c_lt_users}', rampUp: '${c_lt_ramp}', keepUser: false,
      loops: '${c_lt_loops}', duration: '${c_lt_duration}', scheduler: true) {

      insert 'book_offers_ins.groovy'
    }

  // common file-end configuration
  //insert 'fragments/stationary-end.groovy'
  insert 'common/stationary-end.groovy'
  }
}
