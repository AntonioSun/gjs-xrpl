  fragment {
    group name: vf_name, loops: vf_loops, users: vf_users, duration: vf_duration,
      delay: vf_delay, keepUser: false, delayedStart: true, scheduler: true, {

      debug '--== Tx: ledger_closed ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 ledger_closed', generate: true) {

        http (method: 'POST', path: '/', name: 'Tx01r ledger_closed',
              comments: 'https://xrpl.org/ledger_closed.html') {
          body '''{"method":"ledger_closed","params": [{}]}'''
        }

      }

      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: vf_pt_delay, range: vf_pt_range)
      }
    }
  }
