package com.example.seleniumspringbootjava.model.demoblaze;

/**
 * Data model for DemoBlaze "Place Order" modal.
 *
 * Keeps test code clean and avoids long chains of fillX() calls.
 */
public class OrderDetails {

    private final String name;
    private final String country;
    private final String city;
    private final String card;
    private final String month;
    private final String year;

    private OrderDetails(Builder builder) {
        this.name = builder.name;
        this.country = builder.country;
        this.city = builder.city;
        this.card = builder.card;
        this.month = builder.month;
        this.year = builder.year;
    }

    public String name() {
        return name;
    }

    public String country() {
        return country;
    }

    public String city() {
        return city;
    }

    public String card() {
        return card;
    }

    public String month() {
        return month;
    }

    public String year() {
        return year;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private String country;
        private String city;
        private String card;
        private String month;
        private String year;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder card(String card) {
            this.card = card;
            return this;
        }

        public Builder month(String month) {
            this.month = month;
            return this;
        }

        public Builder year(String year) {
            this.year = year;
            return this;
        }

        public OrderDetails build() {
            return new OrderDetails(this);
        }
    }
}
