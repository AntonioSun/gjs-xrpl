    fragment {
      debug '--== Tx: nft_info ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 nft_info', generate: true) {

        http (method: 'POST', path: '/', name: 'Tx01r nft_info') {
          body '''{"method":"nft_info","params": [{"nft_id":"${s_nft_id}"}]}'''
        }

      }

      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: '${c_pt_delay}', range: '${c_tt_range}')
      }
    }
