package software.plusminus.replacer;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
public class TextDetector {

    private static final int REPLACEMENT_CHAR = 0xFFFD;

    public boolean isMostlyText(Path path, int maxBytes, double threshold) {
        byte[] buffer = new byte[maxBytes];
        int bytesRead;

        try (InputStream in = Files.newInputStream(path)) {
            bytesRead = in.read(buffer, 0, maxBytes);
            if (bytesRead == -1) {
                return false; // empty file
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, bytesRead);
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);

        CharBuffer charBuffer;
        try {
            charBuffer = decoder.decode(byteBuffer);
        } catch (CharacterCodingException e) {
            throw new UncheckedIOException(e);
        }

        int estimatedTextualBytes = 0;
        for (int i = 0; i < charBuffer.length();) {
            int codePoint = Character.codePointAt(charBuffer, i);
            i += Character.charCount(codePoint);

            if (isTextual(codePoint)) {
                estimatedTextualBytes += getUtf8ByteLength(codePoint);
            }
        }

        double ratio = (double) estimatedTextualBytes / bytesRead;
        return ratio >= threshold;
    }

    private boolean isTextual(int codePoint) {
        // Bytes that could not be decoded as UTF-8 become the replacement char;
        // a high count of these indicates binary content.
        if (codePoint == REPLACEMENT_CHAR) {
            return false;
        }
        // Common whitespace (space, tab, newline, carriage return) is text.
        if (Character.isWhitespace(codePoint)) {
            return true;
        }
        // Everything else is text only if it is a printable character. Punctuation and
        // symbols (which dominate minified JS/JSON, XML and lock files) are printable and
        // therefore treated as text; NUL and other control chars are not.
        return isPrintable(codePoint);
    }

    private boolean isPrintable(int codePoint) {
        if (Character.isISOControl(codePoint) || !Character.isDefined(codePoint)) {
            return false;
        }
        return isPrintableType(Character.getType(codePoint));
    }

    private boolean isPrintableType(int type) {
        switch (type) {
            case Character.CONTROL:
            case Character.FORMAT:
            case Character.SURROGATE:
            case Character.PRIVATE_USE:
            case Character.UNASSIGNED:
                return false;
            default:
                return true;
        }
    }

    private int getUtf8ByteLength(int codePoint) {
        if (codePoint <= 0x7F) {
            return 1;
        } else if (codePoint <= 0x7FF) {
            return 2;
        } else if (codePoint <= 0xFFFF) {
            return 3;
        } else {
            return 4;
        }
    }
}

