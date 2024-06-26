fragment {
    backend(name: 'InfluxDb Backend', classname: 'rocks.nt.apm.jmeter.JMeterInfluxDBBackendListenerClient', enabled: false) {
      arguments {
        argument(name: 'testName', value: '${c_cfg_TestName}')
        argument(name: 'nodeName', value: '${c_cfg_TestName} - users: ${c_lt_users}, duration ${c_lt_duration}, rampup: ${c_lt_ramp} (${c_app_testdesc})')
        argument(name: 'runId', value: '''${c_cfg_TestName}_${__time(yyMMdd-HHmm)}''')
        argument(name: 'influxDBHost', value: '${c_cfg_Influxdb}')
        argument(name: 'influxDBPort', value: '8086')
        argument(name: 'influxDBUser', value: 'jmwriter')
        argument(name: 'influxDBPassword', value: '61pug4LZ04=NK4d2jLVb1')
        argument(name: 'influxDBDatabase', value: 'jmeter')
        argument(name: 'retentionPolicy', value: 'autogen')
        argument(name: 'samplersList', value: '^Tx\\d+.*')
        argument(name: 'useRegexForSamplerList', value: 'true')
        argument(name: 'recordSubSamples', value: 'true')
      }
    }
}
