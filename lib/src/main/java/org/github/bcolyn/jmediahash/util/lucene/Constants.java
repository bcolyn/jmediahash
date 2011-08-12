package org.github.bcolyn.jmediahash.util.lucene;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Some useful constants.
 **/

public final class Constants {
  private Constants() {}			  // can't construct

    public static final String OS_ARCH = System.getProperty("os.arch");

    // NOTE: this logic may not be correct; if you know of a
  // more reliable approach please raise it on java-dev!
  public static final boolean JRE_IS_64BIT;
  static {
    String x = System.getProperty("sun.arch.data.model");
    if (x != null) {
      JRE_IS_64BIT = x.indexOf("64") != -1;
    } else {
      if (OS_ARCH != null && OS_ARCH.indexOf("64") != -1) {
        JRE_IS_64BIT = true;
      } else {
        JRE_IS_64BIT = false;
      }
    }
  }


}
