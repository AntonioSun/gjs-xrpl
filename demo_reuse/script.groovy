@GrabConfig(systemClassLoader=true)
@Grab('net.simonix.scripts:groovy-jmeter')

@groovy.transform.BaseScript net.simonix.dsl.jmeter.TestScript script

start {
  plan {
    variables {
      // using __env() custom JMeter Functions, https://jmeter-plugins.org/wiki/Functions/
      variable(name: 'c_app_host_name', value: '${__env(c_app_host_name, , localhost)}', description: 'Test server host name')
      variable(name: 'c_app_host_port', value: '${__env(c_app_host_port, , 7070)}', description: 'Test server host port')
      variable(name: 'c_app_protocol', value: '${__env(c_app_protocol, , http)}', description: 'Test server protocol')
      variable(name: 'c_cfg_TestName', value: 'structure-demo', description: 'Test name to identify different tests')
      variable(name: 'c_cfg_Influxdb', value: '${__env(c_cfg_Influxdb, , localhost)}', description: 'Influxdb server host name')
    }

  // common file-beg configuration
  insert 'fragments/stationary-beg.groovy'

  debug '---- Thread Groups starts ----', enabled: false
  group(name: 'TGroup-1', users: 12, rampUp: 30) {
    cookies()

    // insert login fragment
    insert 'fragments/login.groovy'

    http('GET /api/books') {
        params values: [ limit: '10' ]
    }
  }

  insert 'fragments/group.groovy', variables:
     ["var_inner_tg_name": 'TGroup-2', "var_inner_users": 10, "var_inner_duration": 20, "var_inner_loops": -1]

  // common file-end configuration
  insert 'fragments/stationary-end.groovy'
  }
}
