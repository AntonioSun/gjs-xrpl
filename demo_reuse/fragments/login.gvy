fragment {
        http (method: 'POST', path: '/login', name: 'Tx01r login') {
          headers {
            header(name: 'Accept', value: 'text/html, application/xhtml+xml')
          }
          params {
            param(name: 'username', value: 'john')
            param(name: 'password', value: 'john')
          }

          //extract_regex expression: '"trackId", content="([0-9]+)"', variable: 'var_trackId'
        }
}
