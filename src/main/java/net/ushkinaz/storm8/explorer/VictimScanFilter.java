/*
 * Copyright (c) 2010-2010, Dmitry Sidorenko. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ushkinaz.storm8.explorer;

import net.ushkinaz.storm8.domain.Victim;

/**
 * @author Dmitry Sidorenko
 */
public interface VictimScanFilter {
    /**
     * @param victim victim to examine
     * @return {@code true} if this victim should be filtered out (not processed)
     */
    boolean filter(Victim victim);
}