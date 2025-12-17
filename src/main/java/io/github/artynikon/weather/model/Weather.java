package io.github.artynikon.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather {
    private String main;
    private String description;
    private String icon;

    public String getMain() {
        return main;
    }
    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormattedDescription() {
        if (description == null || description.isEmpty()) {
            return "";
        }
        return description.substring(0, 1).toUpperCase() + description.substring(1);
    }

    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
}
