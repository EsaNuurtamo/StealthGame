package game.scores;

public enum Difficulty {
    Easy(0.85f), Normal(1.0f), Hard(1.20f), Impossible(1.5f);

    private float num;

    Difficulty(float num) {
        this.num = num;
    }

    public float getNum() {
        return num;
    }
}
