package io.github.toniidev.toniishops.secret;

public enum Secret {
    PASS("--toniisxs");

    private final String string;

    Secret(String secret){
        this.string = secret;
    }

    public String getString(){
        return this.string;
    }
}
