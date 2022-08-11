package org.requirementsascode.act;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({"org.requirementsascode.act.core"})
public class AllTests {}