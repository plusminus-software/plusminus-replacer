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
        return Character.isLetterOrDigit(codePoint)
                || Character.isWhitespace(codePoint)
                || isEmoji(codePoint);
    }

    private boolean isEmoji(int codePoint) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(codePoint);
        return block == Character.UnicodeBlock.EMOTICONS
                || block == Character.UnicodeBlock.MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS
                || block == Character.UnicodeBlock.TRANSPORT_AND_MAP_SYMBOLS
                || block == Character.UnicodeBlock.DINGBATS;
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

