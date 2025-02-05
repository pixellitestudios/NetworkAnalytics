/*
 * This file is part of NetworkAnalytics, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.networkanalytics;

import lombok.Getter;

import me.lucko.helper.Services;
import me.lucko.helper.messaging.InstanceData;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.sql.Sql;
import me.lucko.networkanalytics.data.DataManager;
import me.lucko.networkanalytics.handler.AnalyticsCommand;
import me.lucko.networkanalytics.handler.AnalyticsListener;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;
public class AnalyticsPlugin extends ExtendedJavaPlugin implements NetworkAnalytics {

    private InstanceData instanceData;

    @Getter
    private DataManager dataManager;

    @Override
    public void enable() {

        // get instance data
        instanceData = Services.get(InstanceData.class).orElseGet(() -> {
            String name = loadConfig("config.yml").getString("server-id", "null");

            return new InstanceData() {
                @Nonnull
                @Override
                public String getId() {
                    return name;
                }

                @Nonnull
                @Override
                public Set<String> getGroups() {
                    return Collections.emptySet();
                }
            };
        });

        // get sql source
        Sql sql = getService(Sql.class);

        // init data manager
        dataManager = new DataManager(this, sql);
        dataManager.init();

        bindModule(new AnalyticsListener(this));

        registerCommand(new AnalyticsCommand(this), "analytics");

        provideService(NetworkAnalytics.class, this);
    }

    public String getInstanceId() {
        return instanceData.getId();
    }

}
