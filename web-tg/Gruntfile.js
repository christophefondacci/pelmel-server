'use strict';

module.exports = function(grunt) {

  // Project configuration.
  grunt.initConfig({
    // Metadata.
    pkg: grunt.file.readJSON('package.json'),
    banner: '/*! <%= pkg.title || pkg.name %> - v<%= pkg.version %> - ' +
      '<%= grunt.template.today("yyyy-mm-dd") %>\n' +
      '<%= pkg.homepage ? "* " + pkg.homepage + "\\n" : "" %>' +
      '* Copyright (c) <%= grunt.template.today("yyyy") %> <%= pkg.author.name %>;' +
      ' Licensed <%= _.pluck(pkg.licenses, "type").join(", ") %> */\n',
    // Task configuration.
    clean: {
      src: ['dist/']
    },
    useminPrepare: {
    	html:'target/web-proto/jsp/structure/default.jsp'
    },
    usemin: {
    	html:'target/web-proto/jsp/structure/default.jsp',
    	options: {
            flow: {
              html: {
                steps: {
                  js: ['concat', 'uglifyjs'],
                  css: ['cssmin']
                },
                post: {}
              }
            },
            assetsDirs: ['dist','dist/styles','dist/js']
          }
    },
    // Renames files for browser caching purposes
    filerev: {
      dist: {
        src: [
          'dist/js/pelmel.min.js',
          'dist/styles/pelmel.min.css',
        ]
      }
    },
    copy: {
    	postBuild: {
    		files: [{expand:true, cwd: 'dist/',src: ['**/*.js','**/*.css'], dest:'../web-static/'}]
    	}
    }
//    	main: {
//    		files: [
//    		        {
//    		        	expand:true, src: [
//    		                            'js/bootstrap/**',
//    		                            'js/crypto/**',
//    		                            'js/jquery/**',
//    		                            'js/openlayers/**',
//    		                            'angular/**',
//    		                            'css/**',
//    		                            'fonts/**',
//    		                            'img/**',
//    		                            'partials/**',
//    		                            'config.js'],
//    		                     dest:'dist/'
//    		        },
//    		        {	expand:true, flatten:true, src: ['index.html'],dest:'dist/'}
//    		        ]
//    	}
//    },
//    qunit: {
//      files: ['test/**/*.html']
//    },
//    jshint: {
//      gruntfile: {
//        options: {
//          jshintrc: '.jshintrc'
//        },
//        src: 'Gruntfile.js'
//      },
//      src: {
//        options: {
//          jshintrc: 'js/.jshintrc'
//        },
//        src: ['js/**/*.js']
//      }
//    },
//    watch: {
//      gruntfile: {
//        files: '<%= jshint.gruntfile.src %>',
//        tasks: ['jshint:gruntfile']
//      },
//      src: {
//        files: '<%= jshint.src.src %>',
//        tasks: ['jshint:src', 'qunit']
//      },
//      test: {
//        files: '<%= jshint.test.src %>',
//        tasks: ['jshint:test', 'qunit']
//      },
//    },
  });

  // These plugins provide necessary tasks.
//  grunt.loadNpmTasks('grunt-contrib-clean');
//  grunt.loadNpmTasks('grunt-contrib-concat');
//  grunt.loadNpmTasks('grunt-contrib-uglify');
//  grunt.loadNpmTasks('grunt-contrib-copy');
//  grunt.loadNpmTasks('grunt-usemin');
//  grunt.loadNpmTasks('grunt-contrib-cssmin');
  require('load-grunt-tasks')(grunt);

  // Default task.
  grunt.registerTask('default', ['clean', 'useminPrepare','concat','cssmin','uglify','filerev','usemin','copy:postBuild']);

};