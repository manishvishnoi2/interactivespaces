/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package interactivespaces.controller.client.node;

/**
 * @author peringknife@google.com (Trevor Pering)
 */
public enum SpaceControllerStatus {

  /**
   * Space controller success.
   */
  SUCCESS("success"),

  /**
   * Space controller failure.
   */
  FAILURE("failure");

  /**
   * String value to use for this constant.
   */
  private final String description;

  SpaceControllerStatus(String description) {
    this.description = description;
  }

  /**
   * Get the descriptive name for this status.
   *
   * @return
   *     descriptive name
   */
  public String getDescription() {
    return description;
  }
}