package com.infa.products.discovery.automatedclassification.engine.search;

import com.infa.products.discovery.automatedclassification.util.Pair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.infa.products.discovery.automatedclassification.util.DiscoveryConstants.*;

public class AlignmentRegex {

    enum RegexGroupType {
        WORD, ABBREVIATION
    }

    private static final Set<Character> SPECIAL_CHARACTERS = new HashSet<>(Arrays.asList('\\', '$', '!', '@', '#', '%', '^', '&', '*', '(', ')', '-', '+', '=', '`', '~', ',', '.', '/', '?', '<', '>', '{', '}', '[', ']', ';', ':', '\"', '\''));

    private Map<Integer, List<RegexCapturingGroup>> sequence = new HashMap<>();

    private List<RegexCapturingGroup> current;

    private int currentPosition = -1;

    private int currentGroupIndex = -1;

    private List<RegexCapturingGroupToken> currentTokenSequenceBuffer = new ArrayList<>();

    private void init() {
        this.current = new ArrayList<>();
        this.currentPosition = 1;
        this.currentGroupIndex = 1;
    }

    public Map<Integer, List<Pair<String, String>>> getRegexQueries() {


        Map<Integer, List<Pair<String, String>>> idCSRegexMap = new TreeMap<>();
        for (Integer i : sequence.keySet()) {
            List<RegexCapturingGroup> groups = sequence.get(i);
            StringBuilder tempRegexBuilder = new StringBuilder();
            StringBuilder tempCSBuilder = new StringBuilder();
            for (int j = 0; j < groups.size(); j++) {
                Pair<String, String> capturedStringRegexPair = new Pair();
                /*
                 * Inner parenthesis begins.
                 */
                tempRegexBuilder.append(OPENING_BRACES);
                //tempRegexBuilder.append(".*");
                List<RegexCapturingGroupToken> tokenSequence = groups.get(j).tokenSequence;
                for (int k = 0; k < tokenSequence.size(); k++) {
                    RegexCapturingGroupToken token = tokenSequence.get(k);
                    if (token.getType().equals(RegexGroupType.WORD)) {
                        String tokenString = token.getToken();
                        tokenString = escapeSpecialChars(tokenString);
                        tempRegexBuilder.append(tokenString).append(".*");
                    }
                        else {
                            for (int m = 0; m < token.getToken().length(); m++) {
                                String tokenString = String.valueOf(token.getToken().charAt(m));
                                tokenString = escapeSpecialChars(tokenString);
                                tempRegexBuilder.append(tokenString).append(".*");
                            }
                        }

                    tempCSBuilder.append(token.getToken()).append(SPACE);
                }
                capturedStringRegexPair.setFirst(tempCSBuilder.toString().trim());
                /*
                 * Inner parenthesis ends.
                 */
                tempRegexBuilder.append(CLOSING_BRACES);
                capturedStringRegexPair.setSecond(tempRegexBuilder.toString());
                if (idCSRegexMap.get(i) == null) {
                    List<Pair<String, String>> tempRegexes = new ArrayList<>();
                    idCSRegexMap.put(i, tempRegexes);
                }
                idCSRegexMap.get(i).add(capturedStringRegexPair);
                tempCSBuilder.delete(0, tempCSBuilder.length());
                tempRegexBuilder.delete(0, tempRegexBuilder.length());
            }
        }
        return idCSRegexMap;
    }

    public String getCapturedString(String matchedString, String regex) {
        Matcher m = getMatcher(matchedString, regex);
        if (!checkMatch(m)) {
            throw new IllegalArgumentException("String not a match for regex");
        }

        StringBuffer capturedString = new StringBuffer();
        for (Integer i : this.sequence.keySet()) {
            if (i > 1) {
                capturedString.append(" ");
            }
            List<RegexCapturingGroup> groups = sequence.get(i);
            for (int j = 0; j < groups.size(); j++) {
                RegexCapturingGroup group = groups.get(j);
                String matchedGroup = m.group(group.groupIndex);
                if (matchedGroup != null) {
                    capturedString.append(group.getTokenSequenceString(" "));
                    /*
                     * Break on first match.
                     */
                    break;
                }
            }
        }
        return capturedString.toString();
    }

    public boolean checkMatch(Matcher m) {
        return m.find();
    }

    public boolean checkMatch(String matchedString, String regex) {
        return checkMatch(getMatcher(matchedString, regex));
    }

    public Matcher getMatcher(String matchedString, String regex) {
        // Create a Pattern object
        Pattern r = Pattern.compile(regex);

        // Now create matcher object.
        Matcher m = r.matcher(matchedString);

        return m;
    }

    public void beginTokenSequence() {
        if (current == null) {
            init();
        }
    }

    public void addToken(String token, RegexGroupType type) {
        this.currentTokenSequenceBuffer.add(new RegexCapturingGroupToken(token, type));
    }

    public void endTokenSequence() {
        RegexCapturingGroup g = new RegexCapturingGroup(
                Collections.unmodifiableList(new ArrayList<>(this.currentTokenSequenceBuffer)));
        if (!this.current.contains(g)) {
            this.current.add(g);
        }
        this.currentTokenSequenceBuffer.clear();
    }

    public void advance() {
        pack();
        this.current = new ArrayList<>();
        this.currentPosition++;
    }

    public void done() {
        pack();
    }

    private void pack() {
        this.current.sort(new Comparator<RegexCapturingGroup>() {

            @Override
            public int compare(RegexCapturingGroup arg0, RegexCapturingGroup arg1) {
                return arg0.compare(arg1);
            }

        });
        if (this.current.size() > 1) {
            this.currentGroupIndex++;
        }
        for (RegexCapturingGroup g : current) {
            g.setGroupIndex(currentGroupIndex++);
        }
        this.sequence.put(currentPosition, this.current);
    }

    class RegexCapturingGroupToken {
        private final String token;
        private final RegexGroupType type;

        public RegexCapturingGroupToken(String token, RegexGroupType type) {
            super();
            this.token = token;
            this.type = type;
        }

        public String getToken() {
            return token;
        }

        public RegexGroupType getType() {
            return type;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((token == null) ? 0 : token.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            RegexCapturingGroupToken other = (RegexCapturingGroupToken) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (token == null) {
                if (other.token != null)
                    return false;
            } else if (!token.equals(other.token))
                return false;
            return true;
        }

        private AlignmentRegex getOuterType() {
            return AlignmentRegex.this;
        }

        @Override
        public String toString() {
            return token + "[" + type + "]";
        }
    }

    class RegexCapturingGroup {
        int groupIndex = -1;
        final List<RegexCapturingGroupToken> tokenSequence;

        public RegexCapturingGroup(List<RegexCapturingGroupToken> tokenSequence) {
            this.tokenSequence = tokenSequence;
        }

        public void setGroupIndex(int groupIndex) {
            this.groupIndex = groupIndex;
        }

        public int getNumberOfWordTokens() {
            return (int) tokenSequence.stream().filter(token -> token.getType().equals(RegexGroupType.WORD)).count();
        }

        public int compare(RegexCapturingGroup g) {
            /*
             * Give priority to the token sequence which contains more known words.
             */
            if (this.getNumberOfWordTokens() > g.getNumberOfWordTokens()) {
                return -1;
            } else if (this.getNumberOfWordTokens() < g.getNumberOfWordTokens()) {
                return 1;
            }
            return 0;
        }

        public String getTokenSequenceString(String tokenSeparator) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < tokenSequence.size(); i++) {
                if (i > 0) {
                    buffer.append(tokenSeparator);
                }
                buffer.append(tokenSequence.get(i).token);
            }
            return buffer.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((tokenSequence == null) ? 0 : tokenSequence.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            RegexCapturingGroup other = (RegexCapturingGroup) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (tokenSequence == null) {
                if (other.tokenSequence != null)
                    return false;
            } else if (!tokenSequence.equals(other.tokenSequence))
                return false;
            return true;
        }

        private AlignmentRegex getOuterType() {
            return AlignmentRegex.this;
        }

        @Override
        public String toString() {
            return groupIndex + ":[" + tokenSequence + "]";
        }
    }

    private String escapeSpecialChars(String value) {
        char[] charcacterArray = value.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (char character : charcacterArray) {
            if (SPECIAL_CHARACTERS.contains(character)) {
                stringBuilder.append(BACKWARD_SLASH).append(character);
            } else {
                stringBuilder.append(character);
            }
        }
        return stringBuilder.toString();
    }

}
