package com.chirput.allwidgets.lnch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class Widget {

        public static final String SPLIT = ",";

        @Nullable
        static Widget from(String s) {
            if (s == null || s.isEmpty()) {
                return null;
            }
            String[] split = s.split(SPLIT);
            if (split.length != 3) {
                return null;
            }
            try {
                return new Widget(
                        Integer.parseInt(split[0]),
                        Integer.parseInt(split[1]),
                        Integer.parseInt(split[2])
                );
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        public final int appWidgetId;
        public final int widthCells;
        public final int heightCells;

        public Widget(int appWidgetId, int widthCells, int heightCells) {
            this.appWidgetId = appWidgetId;
            this.widthCells = widthCells;
            this.heightCells = heightCells;
        }

        void writeTo(StringBuilder sb) {
            sb.append(appWidgetId);
            sb.append(SPLIT);
            sb.append(widthCells);
            sb.append(SPLIT);
            sb.append(heightCells);
        }

        @NonNull
        @Override
        public String toString() {
            return "Widget{" + appWidgetId + ", " + widthCells + "x" + heightCells + "}";
        }
    }