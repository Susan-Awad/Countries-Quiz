package edu.uga.cs.countriesquiz;

public class Country {
    private long id;
    private String country;
    private String capital;
    private String continent;
    private String abbreviation;

    public Country() {
        this.id = -1;
        this.country = null;
        this.capital = null;
        this.continent = null;
        this.abbreviation = null;
    }

    public Country(String country, String capital, String continent, String abbreviation) {
        this.id = -1;
        this.country = country;
        this.capital = capital;
        this.continent = continent;
        this.abbreviation = abbreviation;
    }

    public long getId() {
        return id;
    }

    public long setId(long id) {
        return this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public String setCountry(String country) {
        return this.country = country;
    }

    public String getCapital() {
        return capital;
    }

    public String setCapital(String capital) {
        return this.capital = capital;
    }

    public String getContinent() {
        return continent;
    }
    public String setContinent(String continent) {
        return this.continent = continent;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String setAbbreviation(String abbreviation) {
        return this.abbreviation = abbreviation;
    }

    public String toString() {
        return id + ": " + country + " " + capital + " " + continent + " " + abbreviation;
    }
}
