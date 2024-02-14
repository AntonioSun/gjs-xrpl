fragment {
    group name: var_inner_tg_name, loops: var_inner_loops, users: var_inner_users, duration: var_inner_duration, 
      keepUser: false, delayedStart: true, scheduler: true, {
        cookies(name: 'cookies manager')

        // insert login fragment
        insert 'fragments/login.groovy'

        http 'GET /api/books', {
            params values: [ limit: '10' ]
        }
    }
}