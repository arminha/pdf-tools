package com.aha.pdftools.transform.javamodules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Data class to hold the information that should be added as module-info.class to an existing Jar file.
 */
public class ModuleInfo implements Serializable {
    private final String moduleName;
    private final List<String> exports = new ArrayList<>();
    private final List<String> requires = new ArrayList<>();
    private final List<String> requiresTransitive = new ArrayList<>();

    ModuleInfo(String moduleName) {
        this.moduleName = moduleName;
    }

    public void exports(String exports) {
        this.exports.add(exports);
    }

    public void requires(String requires) {
        this.requires.add(requires);
    }

    public void requiresTransitive(String requiresTransitive) {
        this.requiresTransitive.add(requiresTransitive);
    }

    public String getModuleName() {
        return moduleName;
    }

    protected List<String> getExports() {
        return exports;
    }

    protected List<String> getRequires() {
        return requires;
    }

    protected List<String> getRequiresTransitive() {
        return requiresTransitive;
    }
}
