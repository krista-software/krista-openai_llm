/*
 * Openai Llm Extension for Krista
 * Copyright (C) 2025 Krista Software
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>. 
 */

package app.krista.extensions.krista.llms.openai_qa.factory;

import app.krista.extensions.krista.llms.openai_qa.service.*;
import app.krista.extensions.krista.llms.openai_qa.service.impl.*;
import app.krista.extensions.krista.llms.openai_qa.dto.DiscussionHandler;
import app.krista.extensions.krista.llms.openai_qa.util.KristaMediaClientUtil;
import app.krista.extensions.krista.llms.openai_qa.util.MediaServerFileAccess;

/**
 * Factory for creating discussion-related service instances.
 * Implements Factory pattern for centralized object creation and dependency management.
 */
public class DiscussionFactory {
    
    private static DiscussionFactory instance;
    
    // Cached service instances
    private IDiscussionValidator discussionValidator;
    private IHttpClientFactory httpClientFactory;
    private IDiscussionCatalogService discussionCatalogService;
    
    private DiscussionFactory() {
        // Private constructor for singleton pattern
    }
    
    /**
     * Gets the singleton instance of DiscussionFactory.
     * 
     * @return DiscussionFactory instance
     */
    public static synchronized DiscussionFactory getInstance() {
        if (instance == null) {
            instance = new DiscussionFactory();
        }
        return instance;
    }
    
    /**
     * Creates a discussion service instance with dependency injection.
     *
     * @param discussionHandler The discussion handler dependency
     * @return IDiscussionService implementation
     */
    public IDiscussionService createDiscussionService(DiscussionHandler discussionHandler) {
        return new DiscussionServiceImpl(
            discussionHandler,
            createDiscussionValidator()
        );
    }
    
    /**
     * Creates a file processor instance.
     *
     * @param mediaClientUtil Media client utility dependency
     * @param mediaServerFileAccess Media server file access dependency
     * @return IFileProcessor implementation
     */
    public IFileProcessor createFileProcessor(KristaMediaClientUtil mediaClientUtil,
                                            MediaServerFileAccess mediaServerFileAccess) {
        // Always create a new instance with proper dependencies - don't cache
        return new FileProcessorImpl(mediaClientUtil, mediaServerFileAccess, createDiscussionValidator());
    }
    
    /**
     * Creates a discussion validator instance.
     * 
     * @return IDiscussionValidator implementation
     */
    public IDiscussionValidator createDiscussionValidator() {
        if (discussionValidator == null) {
            discussionValidator = new DiscussionValidatorImpl();
        }
        return discussionValidator;
    }
    
    /**
     * Creates an HTTP client factory instance.
     *
     * @return IHttpClientFactory implementation
     */
    public IHttpClientFactory createHttpClientFactory() {
        if (httpClientFactory == null) {
            httpClientFactory = new HttpClientFactoryImpl();
        }
        return httpClientFactory;
    }

    /**
     * Creates a discussion catalog service instance with all dependencies.
     *
     * @param discussionHandler The discussion handler dependency
     * @param mediaClientUtil Media client utility dependency
     * @param mediaServerFileAccess Media server file access dependency
     * @return IDiscussionCatalogService implementation
     */
    public IDiscussionCatalogService createDiscussionCatalogService(DiscussionHandler discussionHandler,
                                                                   KristaMediaClientUtil mediaClientUtil,
                                                                   MediaServerFileAccess mediaServerFileAccess) {
        if (discussionCatalogService == null) {
            IDiscussionService discussionService = createDiscussionService(discussionHandler);
            IDiscussionValidator validator = createDiscussionValidator();
            IFileProcessor fileProcessor = createFileProcessor(mediaClientUtil, mediaServerFileAccess);

            discussionCatalogService = new DiscussionCatalogServiceImpl(discussionService, validator, fileProcessor);
        }
        return discussionCatalogService;
    }
    
    /**
     * Gets cached discussion validator instance.
     * 
     * @return IDiscussionValidator instance
     */
    private IDiscussionValidator getDiscussionValidator() {
        if (discussionValidator == null) {
            discussionValidator = createDiscussionValidator();
        }
        return discussionValidator;
    }
    

    
    /**
     * Resets factory state for testing purposes.
     */
    public void reset() {
        discussionValidator = null;
        httpClientFactory = null;
        discussionCatalogService = null;
    }
}
