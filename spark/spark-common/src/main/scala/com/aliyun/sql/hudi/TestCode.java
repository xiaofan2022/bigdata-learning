/*
package com.aliyun.sql.hudi;

import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.catalyst.expressions.UnsafeRow;
import org.apache.spark.sql.errors.QueryExecutionErrors;

*/
/**
 * @author twan
 * @version 1.0
 * @description
 * @date 2024-04-16 12:50:47
 *//*

public class TestCode {

    */
/* 001 *//*

    public java.lang.Object generate(Object[] references) {
        */
/* 002 *//*

        return new SpecificUnsafeProjection(references);
        */
/* 003 *//*

    }

    */
/* 004 *//*

 */
/* 005 *//*
 class SpecificUnsafeProjection extends org.apache.spark.sql.catalyst.expressions.UnsafeProjection {
        */
/* 006 *//*

 */
/* 007 *//*
   private final Object[] references;
        */
/* 008 *//*
   private boolean subExprIsNull_0;
        */
/* 009 *//*
   private final java.lang.String[] mutableStateArray_1 = new java.lang.String[3];
        */
/* 010 *//*
   private final org.apache.spark.sql.catalyst.expressions.codegen.UnsafeRowWriter[] mutableStateArray_3 = new org.apache.spark.sql.catalyst.expressions.codegen.UnsafeRowWriter[1];
        */
/* 011 *//*
   private final java.sql.Timestamp[] mutableStateArray_2 = new java.sql.Timestamp[2];
        */
/* 012 *//*
   private final org.apache.spark.sql.Row[] mutableStateArray_0 = new org.apache.spark.sql.Row[1];

        */
/* 013 *//*

 */
/* 014 *//*

        public SpecificUnsafeProjection(Object[] references) {
            */
/* 015 *//*

            this.references = references;
            */
/* 016 *//*

 */
/* 017 *//*

            mutableStateArray_3[0] = new org.apache.spark.sql.catalyst.expressions.codegen.UnsafeRowWriter(6, 96);
            */
/* 018 *//*

 */
/* 019 *//*

        }

        */
/* 020 *//*

 */
/* 021 *//*

        public void initialize(int partitionIndex) {
            */
/* 022 *//*

 */
/* 023 *//*

        }

        */
/* 024 *//*

 */
/* 025 *//*
   // Scala.Function1 need this
        */
/* 026 *//*

        public java.lang.Object apply(java.lang.Object row) {
            */
/* 027 *//*

            return apply((InternalRow) row);
            */
/* 028 *//*

        }

        */
/* 029 *//*

 */
/* 030 *//*

        public UnsafeRow apply(InternalRow i) {
            */
/* 031 *//*

            mutableStateArray_3[0].reset();
            */
/* 032 *//*

            subExpr_0(i);
            */
/* 033 *//*

 */
/* 034 *//*

 */
/* 035 *//*

            writeFields_0_0(i);
            */
/* 036 *//*

            writeFields_0_1(i);
            */
/* 037 *//*

            writeFields_0_2(i);
            */
/* 038 *//*

            return (mutableStateArray_3[0].getRow());
            */
/* 039 *//*

        }

        */
/* 040 *//*

 */
/* 041 *//*

 */
/* 042 *//*

        private void writeFields_0_1(InternalRow i) {
            */
/* 043 *//*

 */
/* 044 *//*

            if (subExprIsNull_0) {
                */
/* 045 *//*

                throw QueryExecutionErrors.inputExternalRowCannotBeNullError();
                */
/* 046 *//*

            }
            */
/* 047 *//*

 */
/* 048 *//*

            if (mutableStateArray_0[0].isNullAt(2)) {
                */
/* 049 *//*

                throw new RuntimeException(((java.lang.String) references[8] */
/* errMsg *//*
));
                */
/* 050 *//*

            }
            */
/* 051 *//*

 */
/* 052 *//*

            final Object value_13 = mutableStateArray_0[0].get(2);
            */
/* 053 *//*

            java.lang.String value_12 = null;
            */
/* 054 *//*

            if (!false) {
                */
/* 055 *//*

                if (value_13 instanceof java.lang.String) {
                    */
/* 056 *//*

                    value_12 = (java.lang.String) value_13;
                    */
/* 057 *//*

                } else {
                    */
/* 058 *//*

 */
/* errMsg *//*

                    throw new RuntimeException(value_13.getClass().getName() + references[7]);
                    */
/* 059 *//*

                }
                */
/* 060 *//*

            }
            */
/* 061 *//*

            mutableStateArray_1[1] = value_12;
            */
/* 062 *//*

 */
/* 063 *//*

 */
/* 064 *//*

            UTF8String value_11 = null;
            */
/* 065 *//*

            if (!false) {
                */
/* 066 *//*

                value_11 = org.apache.spark.unsafe.types.UTF8String.fromString(mutableStateArray_1[1]);
                */
/* 067 *//*

            }
            */
/* 068 *//*

            if (false) {
                */
/* 069 *//*

                throw new NullPointerException(((java.lang.String) references[9] */
/* errMsg *//*
));
                */
/* 070 *//*

            }
            */
/* 071 *//*

            mutableStateArray_3[0].write(2, value_11);
            */
/* 072 *//*

 */
/* 073 *//*

            if (subExprIsNull_0) {
                */
/* 074 *//*

                throw QueryExecutionErrors.inputExternalRowCannotBeNullError();
                */
/* 075 *//*

            }
            */
/* 076 *//*

 */
/* 077 *//*

            if (mutableStateArray_0[0].isNullAt(3)) {
                */
/* 078 *//*

                throw new RuntimeException(((java.lang.String) references[11] */
/* errMsg *//*
));
                */
/* 079 *//*

            }
            */
/* 080 *//*

 */
/* 081 *//*

            final Object value_17 = mutableStateArray_0[0].get(3);
            */
/* 082 *//*

            java.lang.String value_16 = null;
            */
/* 083 *//*

            if (!false) {
                */
/* 084 *//*

                if (value_17 instanceof java.lang.String) {
                    */
/* 085 *//*

                    value_16 = (java.lang.String) value_17;
                    */
/* 086 *//*

                } else {
                    */
/* 087 *//*

 */
/* errMsg *//*

                    throw new RuntimeException(value_17.getClass().getName() + references[10]);
                    */
/* 088 *//*

                }
                */
/* 089 *//*

            }
            */
/* 090 *//*

            mutableStateArray_1[2] = value_16;
            */
/* 091 *//*

 */
/* 092 *//*

 */
/* 093 *//*

            UTF8String value_15 = null;
            */
/* 094 *//*

            if (!false) {
                */
/* 095 *//*

                value_15 = org.apache.spark.unsafe.types.UTF8String.fromString(mutableStateArray_1[2]);
                */
/* 096 *//*

            }
            */
/* 097 *//*

            if (false) {
                */
/* 098 *//*

                throw new NullPointerException(((java.lang.String) references[12] */
/* errMsg *//*
));
                */
/* 099 *//*

            }
            */
/* 100 *//*

            mutableStateArray_3[0].write(3, value_15);
            */
/* 101 *//*

 */
/* 102 *//*

        }

        */
/* 103 *//*

 */
/* 104 *//*

 */
/* 105 *//*

        private void writeFields_0_0(InternalRow i) {
            */
/* 106 *//*

 */
/* 107 *//*

            if (subExprIsNull_0) {
                */
/* 108 *//*

                throw QueryExecutionErrors.inputExternalRowCannotBeNullError();
                */
/* 109 *//*

            }
            */
/* 110 *//*

 */
/* 111 *//*

            if (mutableStateArray_0[0].isNullAt(0)) {
                */
/* 112 *//*

                throw new RuntimeException(((java.lang.String) references[2] */
/* errMsg *//*
));
                */
/* 113 *//*

            }
            */
/* 114 *//*

 */
/* 115 *//*

            final Object value_5 = mutableStateArray_0[0].get(0);
            */
/* 116 *//*

            java.lang.Integer value_4 = null;
            */
/* 117 *//*

            if (!false) {
                */
/* 118 *//*

                if (value_5 instanceof java.lang.Integer) {
                    */
/* 119 *//*

                    value_4 = (java.lang.Integer) value_5;
                    */
/* 120 *//*

                } else {
                    */
/* 121 *//*

 */
/* errMsg *//*

                    throw new RuntimeException(value_5.getClass().getName() + references[1]);
                    */
/* 122 *//*

                }
                */
/* 123 *//*

            }
            */
/* 124 *//*

            boolean isNull_3 = true;
            */
/* 125 *//*

            int value_3 = -1;
            */
/* 126 *//*

            isNull_3 = false;
            */
/* 127 *//*

            if (!isNull_3) {
                */
/* 128 *//*

                value_3 = value_4.intValue();
                */
/* 129 *//*

            }
            */
/* 130 *//*

            if (isNull_3) {
                */
/* 131 *//*

                throw new NullPointerException(((java.lang.String) references[3] */
/* errMsg *//*
));
                */
/* 132 *//*

            }
            */
/* 133 *//*

            mutableStateArray_3[0].write(0, value_3);
            */
/* 134 *//*

 */
/* 135 *//*

            if (subExprIsNull_0) {
                */
/* 136 *//*

                throw QueryExecutionErrors.inputExternalRowCannotBeNullError();
                */
/* 137 *//*

            }
            */
/* 138 *//*

 */
/* 139 *//*

            if (mutableStateArray_0[0].isNullAt(1)) {
                */
/* 140 *//*

                throw new RuntimeException(((java.lang.String) references[5] */
/* errMsg *//*
));
                */
/* 141 *//*

            }
            */
/* 142 *//*

 */
/* 143 *//*

            final Object value_9 = mutableStateArray_0[0].get(1);
            */
/* 144 *//*

            java.lang.String value_8 = null;
            */
/* 145 *//*

            if (!false) {
                */
/* 146 *//*

                if (value_9 instanceof java.lang.String) {
                    */
/* 147 *//*

                    value_8 = (java.lang.String) value_9;
                    */
/* 148 *//*

                } else {
                    */
/* 149 *//*

 */
/* errMsg *//*

                    throw new RuntimeException(value_9.getClass().getName() + references[4]);
                    */
/* 150 *//*

                }
                */
/* 151 *//*

            }
            */
/* 152 *//*

            mutableStateArray_1[0] = value_8;
            */
/* 153 *//*

 */
/* 154 *//*

 */
/* 155 *//*

            UTF8String value_7 = null;
            */
/* 156 *//*

            if (!false) {
                */
/* 157 *//*

                value_7 = org.apache.spark.unsafe.types.UTF8String.fromString(mutableStateArray_1[0]);
                */
/* 158 *//*

            }
            */
/* 159 *//*

            if (false) {
                */
/* 160 *//*

                throw new NullPointerException(((java.lang.String) references[6] */
/* errMsg *//*
));
                */
/* 161 *//*

            }
            */
/* 162 *//*

            mutableStateArray_3[0].write(1, value_7);
            */
/* 163 *//*

 */
/* 164 *//*

        }

        */
/* 165 *//*

 */
/* 166 *//*

 */
/* 167 *//*

        private void subExpr_0(InternalRow i) {
            */
/* 168 *//*

            boolean isNull_1 = i.isNullAt(0);
            */
/* 169 *//*

            org.apache.spark.sql.Row value_1 = isNull_1 ?
                    */
/* 170 *//*
     null : ((org.apache.spark.sql.Row) i.get(0, null));
            */
/* 171 *//*

            if (isNull_1) {
                */
/* 172 *//*

                throw new NullPointerException(((java.lang.String) references[0] */
/* errMsg *//*
));
                */
/* 173 *//*

            }
            */
/* 174 *//*

            subExprIsNull_0 = false;
            */
/* 175 *//*

            mutableStateArray_0[0] = value_1;
            */
/* 176 *//*

        }

        */
/* 177 *//*

 */
/* 178 *//*

 */
/* 179 *//*

        private void writeFields_0_2(InternalRow i) {
            */
/* 180 *//*

 */
/* 181 *//*

            if (subExprIsNull_0) {
                */
/* 182 *//*

                throw QueryExecutionErrors.inputExternalRowCannotBeNullError();
                */
/* 183 *//*

            }
            */
/* 184 *//*

 */
/* 185 *//*

            if (mutableStateArray_0[0].isNullAt(4)) {
                */
/* 186 *//*

                throw new RuntimeException(((java.lang.String) references[14] */
/* errMsg *//*
));
                */
/* 187 *//*

            }
            */
/* 188 *//*

 */
/* 189 *//*

            final Object value_21 = mutableStateArray_0[0].get(4);
            */
/* 190 *//*

            java.sql.Timestamp value_20 = null;
            */
/* 191 *//*

            if (!false) {
                */
/* 192 *//*

                if (value_21 instanceof java.sql.Timestamp || value_21 instanceof java.time.Instant) {
                    */
/* 193 *//*

                    value_20 = (java.sql.Timestamp) value_21;
                    */
/* 194 *//*

                } else {
                    */
/* 195 *//*

 */
/* errMsg *//*

                    throw new RuntimeException(value_21.getClass().getName() + references[13]);
                    */
/* 196 *//*

                }
                */
/* 197 *//*

            }
            */
/* 198 *//*

            mutableStateArray_2[0] = value_20;
            */
/* 199 *//*

 */
/* 200 *//*

 */
/* 201 *//*

            long value_19 = -1L;
            */
/* 202 *//*

            if (!false) {
                */
/* 203 *//*

                value_19 = org.apache.spark.sql.catalyst.util.DateTimeUtils.fromJavaTimestamp(mutableStateArray_2[0]);
                */
/* 204 *//*

            }
            */
/* 205 *//*

            if (false) {
                */
/* 206 *//*

                throw new NullPointerException(((java.lang.String) references[15] */
/* errMsg *//*
));
                */
/* 207 *//*

            }
            */
/* 208 *//*

            mutableStateArray_3[0].write(4, value_19);
            */
/* 209 *//*

 */
/* 210 *//*

            if (subExprIsNull_0) {
                */
/* 211 *//*

                throw QueryExecutionErrors.inputExternalRowCannotBeNullError();
                */
/* 212 *//*

            }
            */
/* 213 *//*

 */
/* 214 *//*

            if (mutableStateArray_0[0].isNullAt(5)) {
                */
/* 215 *//*

                throw new RuntimeException(((java.lang.String) references[17] */
/* errMsg *//*
));
                */
/* 216 *//*

            }
            */
/* 217 *//*

 */
/* 218 *//*

            final Object value_25 = mutableStateArray_0[0].get(5);
            */
/* 219 *//*

            java.sql.Timestamp value_24 = null;
            */
/* 220 *//*

            if (!false) {
                */
/* 221 *//*

                if (value_25 instanceof java.sql.Timestamp || value_25 instanceof java.time.Instant) {
                    */
/* 222 *//*

                    value_24 = (java.sql.Timestamp) value_25;
                    */
/* 223 *//*

                } else {
                    */
/* 224 *//*

 */
/* errMsg *//*

                    throw new RuntimeException(value_25.getClass().getName() + references[16]);
                    */
/* 225 *//*

                }
                */
/* 226 *//*

            }
            */
/* 227 *//*

            mutableStateArray_2[1] = value_24;
            */
/* 228 *//*

 */
/* 229 *//*

 */
/* 230 *//*

            long value_23 = -1L;
            */
/* 231 *//*

            if (!false) {
                */
/* 232 *//*

                value_23 = org.apache.spark.sql.catalyst.util.DateTimeUtils.fromJavaTimestamp(mutableStateArray_2[1]);
                */
/* 233 *//*

            }
            */
/* 234 *//*

            if (false) {
                */
/* 235 *//*

                throw new NullPointerException(((java.lang.String) references[18] */
/* errMsg *//*
));
                */
/* 236 *//*

            }
            */
/* 237 *//*

            mutableStateArray_3[0].write(5, value_23);
            */
/* 238 *//*

 */
/* 239 *//*

        }
        */
/* 240 *//*

 */
/* 241 *//*

    }
}
*/
