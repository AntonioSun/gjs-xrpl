  fragment {

    group name: vf_name, enabled: vf_enabled, loops: vf_loops, users: vf_users, duration: vf_duration,
      rampUp: vf_rampUp, delay: vf_delay, keepUser: false, delayedStart: true, scheduler: true, {

      debug '--== Tx: server_info ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      // transaction('Tx01 server_info', generate: true) {

        http (method: 'POST', path: '/', name: 'Tx01r server_info',
              comments: 'https://xrpl.org/server_info.html') {
          body '''{"method":"server_info"}'''

          extract_jmes expression: 'result.error', variable: 'p_error'
          extract_jmes expression: 'result.engine_result', variable: 'p_result'
        }

      // }
      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: vf_pt_delay, range: vf_pt_range)
      }
    }
  }
