/*
 * JBoss, Home of Professional Open Source
 *  Copyright ${year}, Red Hat, Inc., and individual contributors
 *  by the @authors tag. See the copyright.txt in the distribution for a
 *  full listing of individual contributors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wildfly.extension.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.jboss.as.controller.services.path.AbsolutePathService;
import org.jboss.as.controller.services.path.PathManager;
import org.jboss.msc.inject.Injector;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

/**
 * @author Heiko Braun
 */
public class SparkService implements Service<SparkService> {


    private final InjectedValue<PathManager> pathManager = new InjectedValue<PathManager>();

    private JavaSparkContext ctx;
    private final String clusterName;

    public SparkService(String clusterName) {
        this.clusterName = clusterName;
    }

    @Override
    public SparkService getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }

    @Override
    public void start(StartContext context) throws StartException {

        try {
            SparkLogger.LOGGER.infof("Starting embedded spark service '%s'", clusterName);

            SparkConf sparkConf = new SparkConf().setAppName("Spark On Wildfly");
            sparkConf.setMaster("local[2]");
            ctx = new JavaSparkContext(sparkConf);

        } catch (Throwable e) {
            context.failed(new StartException(e));
        }
    }

    @Override
    public void stop(StopContext context) {
        SparkLogger.LOGGER.infof("Stopping spark service '%s'.", clusterName);
        if(ctx!=null)
            ctx.stop();
    }

    public Injector<PathManager> getPathManagerInjector(){
        return pathManager;
    }

    private String resolve(PathManager pathManager, String path, String relativeToPath) {
        // discard the relativeToPath if the path is absolute and must not be resolved according
        // to the default relativeToPath value
        String relativeTo = AbsolutePathService.isAbsoluteUnixOrWindowsPath(path) ? null : relativeToPath;
        return pathManager.resolveRelativePathEntry(path, relativeTo);
    }

}
