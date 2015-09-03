'use strict';

module.exports = function(grunt) {

	// Project configuration.
	grunt
			.initConfig({
				// Metadata.
				pkg : grunt.file.readJSON('package.json'),
				banner : '/*! <%= pkg.title || pkg.name %> - v<%= pkg.version %> - '
						+ '<%= grunt.template.today("yyyy-mm-dd") %>\n'
						+ '<%= pkg.homepage ? "* " + pkg.homepage + "\\n" : "" %>'
						+ '* Copyright (c) <%= grunt.template.today("yyyy") %> <%= pkg.author.name %>;'
						+ ' Licensed <%= _.pluck(pkg.licenses, "type").join(", ") %> */\n',
				// Task configuration.
				clean : {
					src : [ 'dist/' ]
				},
				useminPrepare : {
					html : ['target/web-proto/jsp/structure/homepage.jsp','target/web-proto/jsp/structure/default.jsp']
				},
				usemin : {
					html : ['target/web-proto/jsp/structure/homepage.jsp','target/web-proto/jsp/structure/default.jsp'],
					options : {
						flow : {
							html : {
								steps : {
									js : [ 'concat', 'uglifyjs' ],
									css : [ 'cssmin' ]
								},
								post : {}
							}
						},
						assetsDirs : [ 'dist', 'dist/styles', 'dist/js' ]
					}
				},
				// Renames files for browser caching purposes
				filerev : {
					dist : {
						src : [ 'dist/js/pelmel.min.js','dist/js/pelmel-hp.min.js',
								'dist/styles/pelmel.min.css', ]
					}
				},
				copy : {
					resources: {
						files : [ {
							expand : true,
							cwd : '../web-static/',
							src : ['fonts/**/*','images/**/*','js/html5shiv.js','js/respond.min.js'],
							dest : 'dist/'
						}] 
					} ,
					postBuild : {
						files : [ {
							expand : true,
							cwd : 'dist/',
							src : [ '**/*' ],
							dest : '../web-static/dist'
						},{
							expand : true,
							cwd : '../web-static/',
							src : ['styles/font-awesome.min.css'],
							dest : '../web-static/dist/'
						}]
					}
				},
				imagemin : {
					dist : {
						files : [ {
							expand : true,
							cwd : 'dist',
							src : 'images/**/*.{png,jpg,jpeg,gif}',
							dest : 'dist'
						} ]
					}
				},
				cdnify : {
					pelmel : {
						options : {
							// base: '//static.pelmelguide.com/',
							rewriter : function(url) {
								console.log('Processing URL \'' + url + '\'');
								if (url.indexOf('/images') === 0
										|| url.indexOf('/styles') === 0
										|| url.indexOf('/fonts') === 0
										|| url.indexOf('/icons') === 0
										|| url.indexOf('/js') === 0) {
									return 'http://static.pelmelguide.com' + url; // add
																				// query
																				// string
																				// to
																				// all
																				// other
																				// URLs
								} else {
									return url; // leave data URIs untouched
								}
							}
						},
						files : [ {
							expand : true,
							cwd : 'target/web-proto',
							src : '**/*.{css,html,jsp,js}',
							dest : 'target/web-proto'
						}]
					},
					css: {
						options : {
							// base: '//static.pelmelguide.com/',
							rewriter : function(url) {
								console.log('Processing URL \'' + url + '\'');
								if (url.indexOf('/images') === 0
										|| url.indexOf('/styles') === 0
										|| url.indexOf('/fonts') === 0
										|| url.indexOf('/icons') === 0
										|| url.indexOf('/js') === 0) {
									return '//static.pelmelguide.com' + url; // add
																				// query
																				// string
																				// to
																				// all
																				// other
																				// URLs
								} else {
									return url; // leave data URIs untouched
								}
							}
						},
						files : [{
							expand:true,
							cwd : 'dist/',
							src : '**/*.css',
							dest : 'dist'
						}]
					}
				}
			});

	// These plugins provide necessary tasks.
	require('load-grunt-tasks')(grunt);

	// Default task.
	grunt.registerTask('default', [ 'clean', 'useminPrepare', 'concat',
			'cssmin', 'uglify', 'cdnify:css', 'filerev', 'usemin', 'copy:resources', 'imagemin','cdnify:pelmel','copy:postBuild' ]);

};