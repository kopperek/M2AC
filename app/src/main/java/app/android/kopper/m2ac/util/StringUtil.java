/*
 * Created by kopper on 2015-05-23.
 * (C) Copyright 2015 kopperek@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package app.android.kopper.m2ac.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringUtil {

    public static String removeSymbols(String s) {
        return s.replaceAll("[^a-zA-Z0-9\\-]","-");
    }

    public static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str.replace('ł','l').replace('Ł','L'),Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    public static String makeIOFrendlyName(String string) {
        return removeSymbols(deAccent(string));
    }
}
