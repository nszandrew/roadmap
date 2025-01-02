package br.com.nszandrew.roadmap.model.AI;

public enum PlanLimit {
    FREE(5),
    BASIC(30),
    PREMIUM(200);

    private final int maxCalls;

    PlanLimit(int maxCalls) {
        this.maxCalls = maxCalls;
    }

    public int getMaxCalls() {
        return maxCalls;
    }
}
