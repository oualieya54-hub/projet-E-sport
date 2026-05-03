package com.esport.models;

public enum TournamentFormat {
    SINGLE_ELIMINATION("Single Elimination"),
    DOUBLE_ELIMINATION("Double Elimination"),
    ROUND_ROBIN("Round Robin"),
    SWISS("Swiss");

    private final String label;
    TournamentFormat(String label) { this.label = label; }
    public String getLabel() { return label; }
}
