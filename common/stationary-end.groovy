fragment {
    debug '---- Thread Groups ends ----', enabled: false
    insert 'common/backend.groovy'
    summary(file: '''${c_cfg_TestName}_${__time(yyMMdd-HHmm)}.jtl''', enabled: true)
    view () // View Result Tree
}
