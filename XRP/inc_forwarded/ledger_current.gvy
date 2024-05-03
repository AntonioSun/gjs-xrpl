  fragment {
    group name: vf_name, enabled: vf_enabled, loops: vf_loops, users: vf_users, duration: vf_duration, rampUp: vf_rampUp,
      delay: vf_delay, keepUser: false, delayedStart: true, scheduler: true, {

      debug '--== Tx: ledger_current ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 ledger_current', generate: true) {

        http (method: 'POST', path: '/', name: 'Tx01r ledger_current',
              comments: 'https://xrpl.org/ledger_current.html') {
          body '''{"method":"ledger_current","params": [{}]}'''
        }

      }

      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: vf_pt_delay, range: vf_pt_range)
      }
    }
  }
