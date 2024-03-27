    fragment {

      // Defines a 'Random Variable' config element
      random name: 'Random ledger index number', variable: 'ledgerIndex', minimum: 30000000, maximum: 85478162, perUser: true

      debug '--== Tx: Tx_AccountDelete ==--', displayJMeterVariables: true, displayJMeterProperties: true, enabled: false
      transaction('Tx01 Tx_AccountDelete', generate: true) {
        
        http (method: 'POST', path: '/', name: 'Tx01r Tx_AccountDelete, random ledger',
              comments: 'https://xrpl.org/Tx_AccountDelete.html') {
          body '''{"method":"Tx_AccountDelete","params": [{"ledger_index": "${ledgerIndex}","taker": "${acct}","taker_gets": {"currency": "${vCurrency}","issuer": "${vIssuer}"},"taker_pays": {"currency": "XRP"}}]}'''
          //extract_jmes expression: 'book.id', variable: 'p_bookId'
        }
    
      }

      flow (name: 'Think Time Flow Control', enabled: false) {
        uniform_timer (name: 'Think Time', delay: '${c_tt_delay}', range: '${c_tt_range}')
      }

      flow (name: 'Pace Time Flow Control') {
        uniform_timer (name: 'Pace Time', delay: '${c_pt_delay}', range: '${c_pt_range}')
      }

    }