package net.ushkinaz.storm8.domain;

import com.db4o.config.annotations.Indexed;

/**
 * Date: 27.05.2010
 * Created by Dmitry Sidorenko.
 */
public class Identifiable {
// ------------------------------ FIELDS ------------------------------

    @Indexed
    private String id;

// --------------------------- CONSTRUCTORS ---------------------------

    public Identifiable() {
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
