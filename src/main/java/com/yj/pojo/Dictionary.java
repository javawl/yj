package com.yj.pojo;

public class Dictionary {
    private Integer id;

    private String word;

    private String meaning;

    private String phoneticSymbolEn;

    private String pronunciationEn;

    private String sentence;

    private String pic;

    private String tv;

    private Integer type;

    private String phoneticSymbolUs;

    private String pronunciationUs;

    private String phoneticSymbol;

    private String pronunciation;

    private String sentenceCn;

    private String sentenceAudio;

    private String realMeaning;

    private String phoneticSymbolEnMumbler;

    private String pronunciationEnMumbler;

    private String phoneticSymbolUsMumbler;

    private String pronunciationUsMumbler;

    private String meaningMumbler;

    public Dictionary(Integer id, String word, String meaning, String phoneticSymbolEn, String pronunciationEn, String sentence, String pic, String tv, Integer type, String phoneticSymbolUs, String pronunciationUs, String phoneticSymbol, String pronunciation, String sentenceCn, String sentenceAudio, String realMeaning, String phoneticSymbolEnMumbler, String pronunciationEnMumbler, String phoneticSymbolUsMumbler, String pronunciationUsMumbler, String meaningMumbler) {
        this.id = id;
        this.word = word;
        this.meaning = meaning;
        this.phoneticSymbolEn = phoneticSymbolEn;
        this.pronunciationEn = pronunciationEn;
        this.sentence = sentence;
        this.pic = pic;
        this.tv = tv;
        this.type = type;
        this.phoneticSymbolUs = phoneticSymbolUs;
        this.pronunciationUs = pronunciationUs;
        this.phoneticSymbol = phoneticSymbol;
        this.pronunciation = pronunciation;
        this.sentenceCn = sentenceCn;
        this.sentenceAudio = sentenceAudio;
        this.realMeaning = realMeaning;
        this.phoneticSymbolEnMumbler = phoneticSymbolEnMumbler;
        this.pronunciationEnMumbler = pronunciationEnMumbler;
        this.phoneticSymbolUsMumbler = phoneticSymbolUsMumbler;
        this.pronunciationUsMumbler = pronunciationUsMumbler;
        this.meaningMumbler = meaningMumbler;
    }

    public Dictionary() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word == null ? null : word.trim();
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning == null ? null : meaning.trim();
    }

    public String getPhoneticSymbolEn() {
        return phoneticSymbolEn;
    }

    public void setPhoneticSymbolEn(String phoneticSymbolEn) {
        this.phoneticSymbolEn = phoneticSymbolEn == null ? null : phoneticSymbolEn.trim();
    }

    public String getPronunciationEn() {
        return pronunciationEn;
    }

    public void setPronunciationEn(String pronunciationEn) {
        this.pronunciationEn = pronunciationEn == null ? null : pronunciationEn.trim();
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence == null ? null : sentence.trim();
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic == null ? null : pic.trim();
    }

    public String getTv() {
        return tv;
    }

    public void setTv(String tv) {
        this.tv = tv == null ? null : tv.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPhoneticSymbolUs() {
        return phoneticSymbolUs;
    }

    public void setPhoneticSymbolUs(String phoneticSymbolUs) {
        this.phoneticSymbolUs = phoneticSymbolUs == null ? null : phoneticSymbolUs.trim();
    }

    public String getPronunciationUs() {
        return pronunciationUs;
    }

    public void setPronunciationUs(String pronunciationUs) {
        this.pronunciationUs = pronunciationUs == null ? null : pronunciationUs.trim();
    }

    public String getPhoneticSymbol() {
        return phoneticSymbol;
    }

    public void setPhoneticSymbol(String phoneticSymbol) {
        this.phoneticSymbol = phoneticSymbol == null ? null : phoneticSymbol.trim();
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation == null ? null : pronunciation.trim();
    }

    public String getSentenceCn() {
        return sentenceCn;
    }

    public void setSentenceCn(String sentenceCn) {
        this.sentenceCn = sentenceCn == null ? null : sentenceCn.trim();
    }

    public String getSentenceAudio() {
        return sentenceAudio;
    }

    public void setSentenceAudio(String sentenceAudio) {
        this.sentenceAudio = sentenceAudio == null ? null : sentenceAudio.trim();
    }

    public String getRealMeaning() {
        return realMeaning;
    }

    public void setRealMeaning(String realMeaning) {
        this.realMeaning = realMeaning == null ? null : realMeaning.trim();
    }

    public String getPhoneticSymbolEnMumbler() {
        return phoneticSymbolEnMumbler;
    }

    public void setPhoneticSymbolEnMumbler(String phoneticSymbolEnMumbler) {
        this.phoneticSymbolEnMumbler = phoneticSymbolEnMumbler == null ? null : phoneticSymbolEnMumbler.trim();
    }

    public String getPronunciationEnMumbler() {
        return pronunciationEnMumbler;
    }

    public void setPronunciationEnMumbler(String pronunciationEnMumbler) {
        this.pronunciationEnMumbler = pronunciationEnMumbler == null ? null : pronunciationEnMumbler.trim();
    }

    public String getPhoneticSymbolUsMumbler() {
        return phoneticSymbolUsMumbler;
    }

    public void setPhoneticSymbolUsMumbler(String phoneticSymbolUsMumbler) {
        this.phoneticSymbolUsMumbler = phoneticSymbolUsMumbler == null ? null : phoneticSymbolUsMumbler.trim();
    }

    public String getPronunciationUsMumbler() {
        return pronunciationUsMumbler;
    }

    public void setPronunciationUsMumbler(String pronunciationUsMumbler) {
        this.pronunciationUsMumbler = pronunciationUsMumbler == null ? null : pronunciationUsMumbler.trim();
    }

    public String getMeaningMumbler() {
        return meaningMumbler;
    }

    public void setMeaningMumbler(String meaningMumbler) {
        this.meaningMumbler = meaningMumbler == null ? null : meaningMumbler.trim();
    }
}