package org.springframework.exam.config;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nz.net.ultraq.thymeleaf.LayoutDialect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.util.ArrayUtils;

@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Import( value ={WebSocketConfig.class, TailFileConfig.class} )
@ComponentScan( basePackages = "org.springframework.exam", excludeFilters={ @ComponentScan.Filter(Configuration.class) } )
public class WebMvcConfig extends WebMvcConfigurerAdapter {
	@Value("#{systemProperties['spring.profiles.active']}")
	private String springProfilesActive;

	@Autowired
	private ApplicationContext applicationContext;
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/public-resources/");
		registry.addResourceHandler("/js/**").addResourceLocations("/public-resources/js/");
	}
	
	@Bean
	public LocaleResolver localeResolver() {
		return new FixedLocaleResolver(Locale.KOREA);
	}

	@Bean
	public ContentNegotiatingViewResolver contentNegotiationgViewResolver() {
		ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
		viewResolver.setContentNegotiationManager(contentNegotiationManager());

		List<ViewResolver> viewResolvers = new ArrayList<>();
		viewResolvers.add(thymeleafViewResolver());
		viewResolver.setViewResolvers(viewResolvers);
		
		List<View> defaultViews = new ArrayList<>();
		MappingJackson2JsonView mappingJackson2JsonView = new MappingJackson2JsonView();
		mappingJackson2JsonView.setExtractValueFromSingleKeyModel(true);
		mappingJackson2JsonView.setModelKey("result");
		defaultViews.add(mappingJackson2JsonView);

		viewResolver.setDefaultViews(defaultViews);
		return viewResolver;
	}

	@Bean
	public ContentNegotiationManager contentNegotiationManager() {
		Map<String, MediaType> mediaTypes = new HashMap<>();
		mediaTypes.put("html", MediaType.TEXT_HTML);
		mediaTypes.put("json", MediaType.APPLICATION_JSON);
		ContentNegotiationStrategy contentNegotiationStrategy = new PathExtensionContentNegotiationStrategy(mediaTypes);
		return new ContentNegotiationManager(contentNegotiationStrategy);
	}

	@Bean
	public ServletContextTemplateResolver templateResolver() {
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
		templateResolver.setPrefix("/WEB-INF/views/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("HTML5");
		templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
		if (!ArrayUtils.contains(applicationContext.getEnvironment().getActiveProfiles(), "live")) {
			templateResolver.setCacheable(false);
		}
		return templateResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(templateResolver());
		// thymeleaf-layout-dialect 사용 설정
		engine.addDialect(new LayoutDialect());
		return engine;
	}

	@Bean
	public ThymeleafViewResolver thymeleafViewResolver() {
		ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
		thymeleafViewResolver.setTemplateEngine(templateEngine());
		thymeleafViewResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
		if (!ArrayUtils.contains(applicationContext.getEnvironment().getActiveProfiles(), "live")) {
			thymeleafViewResolver.setCache(false);
		}
		return thymeleafViewResolver;
	}
}
