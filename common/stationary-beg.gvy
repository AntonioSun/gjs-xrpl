fragment {
    debug '---- default request settings ----', enabled: false
    defaults(protocol: '${c_app_protocol}', domain: '${c_app_host_name}', port:  '${c_app_host_port}')
    headers {
      header(name: 'Content-Type', value: 'application/json')
    }
    // headers {
    //   header(name: 'Host', value: '${c_app_host_name}')
    //   header(name: 'Origin', value: '${c_app_host_name}')
    //   header(name: 'Referer', value: '${c_app_host_name}')
    //   header(name: 'Connection', value: 'keep-alive')
    //   header(name: 'Cache-Control', value: 'max-age=0')
    //   header(name: 'Upgrade-Insecure-Requests', value: '1')
    //   header(name: 'User-Agent', value: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36')
    // }
    // cookies()
    // cache()

    debug '---- default error checkings ----', enabled: false
    check_response {
      status() eq 200 or 302
    }
    // check_response applyTo: 'parent', {
    //   text() excludes '${c_app_error_kw}'
    // }
}
