  fragment {

    group name: vf_name, loops: vf_loops, users: vf_users, duration: vf_duration,
      rampUp: vf_rampUp, delay: vf_delay, keepUser: false, delayedStart: true, scheduler: true, {

      debug '--== Tx: fee ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 fee', generate: true) {

        http (method: 'POST', path: '/', name: 'Tx01r fee',
              comments: 'https://xrpl.org/fee.html') {
          body '''{"method":"fee","params": [{}]}'''

          extract_jmes expression: 'result.error', variable: 'p_error'
          extract_jmes expression: 'result.engine_result', variable: 'p_result'
        }

      }

      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: '${c_pt_delay}', range: '${c_tt_range}')
      }
    }
  }
