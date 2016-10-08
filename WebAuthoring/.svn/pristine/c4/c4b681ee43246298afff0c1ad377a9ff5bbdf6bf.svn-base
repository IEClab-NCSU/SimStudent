/**
 * @fileoverview Sets up and executes tasks using grunt @link http://gruntjs.com/
 * @author $Author: mringenb $
 * @version $Revision: 21634 $
 */
/*
 * $Date$
 * $HeadURL$

 -
 License:
 -
 ChangeLog:
 -
 Notes:
	To setup and use this, check out the instructions at
	http://gruntjs.com/getting-started

	Quick Instructions:
	1. $ npm update -g npm
	2. $ npm install -g grunt-cli
	3. $ npm install
	4. $ grunt

	If any closure-compiler builds fail and it complains that it is unable to
	access a jarfile, try running: $ grunt shell:build_compiler

	Targets:
	* <null> (default): builds the three main javascript bundles
	* tutor: builds the tutor interface bundle.
	* sidebar: builds the tutor interface bundle for Google's side bar.
	* tracer: builds the CTAT tracer bundle.
	* dep: builds the dependency file necessary to run straight off the source code.
	* test: run unit tests.
	* deploy: copy necessary files for running a site to ./dist/
 */
module.exports = function(grunt) {
	grunt.initConfig({
		pkg: grunt.file.readJSON('package.json'), // read settings from package
		dir: { // commonly referenced directories
			src: "src",
			output: 'ctatjslib',
			deploy: 'dist',
			dep: 'CTAT-deps.js',
			tracer: '<%= dir.src %>/CTATExampleTracer',
			google: 'third-party/google',
			closure_compiler: '<%= dir.google %>/closure-compiler',
			closure_library: '<%= dir.google %>/closure-library'
		},
		cmd: {
			depswriter: '<%= dir.closure_library %>/closure/bin/build/depswriter.py'
		},
		source: { // Source code listings
			CTAT: '<%= dir.src %>/CTAT/**.js',
			CTATCommunication: '<%= dir.src %>/CTATCommunication/**.js',
			CTATComponentHierarchy:'<%= dir.src %>/CTATComponentHierarchy/**.js',
			CTATComponents: '<%= dir.src %>/CTATComponents/**.js',
			CTATMath: '<%= dir.src %>/CTATMath/**.js',
			CTATSerialization: '<%= dir.src %>/CTATSerialization/**.js',
			CTATUtil: '<%= dir.src %>/CTATUtil/**.js',
			CTATJavaConnection: '<%= dir.src %>/CTATJavaConnection/**.js',
			base: ['<%= dir.src %>/CTATBinaryImages.js',
			       '<%= dir.src %>/CTATCanvasComponent.js',
			       '<%= dir.src %>/CTATContextData.js',
			       '<%= dir.src %>/CTATCSS.js',
			       //'<%= dir.src %>/DateTime.js',
			       '<%= dir.src %>/CTATDialogBox.js',
			       //'<%= dir.src %>/CTATExampleTracer_Skills.js',
			       //'<%= dir.src %>/CTATExampleTracer.js',
			       //'<%= dir.src %>/CTATExternals.js',
			       '<%= dir.src %>/CTATFlashVars.js',
			       '<%= dir.src %>/CTATGlobalFunctions.js',
			       '<%= dir.src %>/CTATGlobals.js',
			       '<%= dir.src %>/CTATGraphicsTools.js',
			       '<%= dir.src %>/CTATHTMLManager.js',
			       '<%= dir.src %>/CTATLinkData.js',
			       '<%= dir.src %>/CTATMobileTutorHandler.js',
			       '<%= dir.src %>/CTATScrim.js',
			       '<%= dir.src %>/CTATShape.js',
			       '<%= dir.src %>/CTATStringUtil.js',
			       '<%= dir.src %>/CTATStyle.js',
			       '<%= dir.src %>/CTATTools.js',
			       '<%= dir.src %>/TutorShopAPIProxy.js'],
	       tutor: ['<%= dir.src %>/CTATTutor.js',
	               '<%= source.base %>',
	               '<%= source.CTAT %>',
	               '<%= source.CTATCommunication %>',
				   '<%= source.CTATJavaConnection %>,
	               '<%= source.CTATComponentHierarchy %>',
	               '<%= source.CTATComponents %>', '!<%= dir.src %>/CTATComponents/CTATTableGoogle.js',
	               '<%= source.CTATMath %>',
	               '<%= source.CTATSerialization %>',
	               '<%= source.CTATUtil %>'],
	       sidebar: ['<%= source.tutor %>', '!<%= dir.src %>/CTATTutor.js',
	                 '<% dir.src %?/CTATTutorGoogleSideBar.js'],
	       tracer: ['<%= source.tutor %>',
	                '<%= dir.src %>/polyfills.js',
	    			'<%= dir.tracer %>/NopeJS.js',
	    			'<%= dir.tracer %>/CTATHintPolicyEnum.js',
	    			'<%= dir.tracer %>/CTATExampleTracerException.js',
	    			'<%= dir.tracer %>/CTATMsgType.js',
	    			'<%= dir.tracer %>/CTATVariableTable.js',
	    			'<%= dir.tracer %>/CTATExampleTracerLink.js',
	    			'<%= dir.tracer %>/CTATLinkGroup.js',
	    			'<%= dir.tracer %>/CTATVersionComparator.js',
	    			'<%= dir.tracer %>/CTATExampleTracerGraphVisualData.js',
	    			'<%= dir.tracer %>/CTATExampleTracerLinkVisualData.js',
	    			'<%= dir.tracer %>/CTATExampleTracerNodeVisualData.js',
	    			'<%= dir.tracer %>/CTATExampleTracerTracer.js',
	    			'<%= dir.tracer %>/CTATMatcher.js',
	    			'<%= dir.tracer %>/CTATAnyMatcher.js',
	    			'<%= dir.tracer %>/CTATExactMatcher.js',
	    			'<%= dir.tracer %>/CTATRegexMatcher.js',
	    			'<%= dir.tracer %>/CTATMatcherComparator.js',
	    			'<%= dir.tracer %>/CTATExampleTracerSAI.js',
	    			'<%= dir.tracer %>/CTATExampleTracerSkill.js',
	    			'<%= dir.tracer %>/CTATSkills.js',
	    			'<%= dir.tracer %>/CTATDefaultLinkGroup.js',
	    			'<%= dir.tracer %>/CTATExampleTracerNode.js',
	    			'<%= dir.tracer %>/CTATExampleTracerPathComparator.js',
	    			'<%= dir.tracer %>/CTATExampleTracerEvent.js',
	    			'<%= dir.tracer %>/CTATExpressionMatcher.js',
	    			'<%= dir.tracer %>/CTATGroupIterator.js',
	    			'<%= dir.tracer %>/CTATGroupModel.js',
	    			'<%= dir.tracer %>/CTATRangeMatcher.js',
	    			'<%= dir.tracer %>/CTATExampleTracerPath.js',
	    			'<%= dir.tracer %>/CTATProblemSummary.js',
	    			'<%= dir.tracer %>/CTATWildcardMatcher.js',
	    			'<%= dir.tracer %>/CTATExampleTracerInterpretationComparator.js',
	    			'<%= dir.tracer %>/CTATExampleTracerLinkComparator.js',
	    			'<%= dir.tracer %>/CTATVectorMatcher.js',
	    			'<%= dir.tracer %>/CTATExampleTracerInterpretation.js',
	    			'<%= dir.tracer %>/CTATDefaultGroupModel.js',
	    			'<%= dir.tracer %>/CTATExampleTracerGraph.js',
	    			'<%= dir.tracer %>/CTATGraphParser.js',
	                '<%= dir.src %>/CTATExampleTracer.js']
		},
		'closure-compiler': { // builds JavaScript bundles using Google's closure compiler
			tutor: {
				closurePath: '<%= dir.closure_compiler %>',
				js: '<%= source.tutor %>',
				jsOutputFile: "<%= dir.output %>/g-<%= pkg.name %>-tutor.min.js",
				externs: "<%= dir.src %>/CTATExternals.js",
				maxBuffer: 600,
				options: {
					//compilation_level: 'SIMPLE', //'WHITESPACE_ONLY,ADVANCED
					warning_level: 'DEFAULT', //QUIET, DEFAULT, VERBOSE
					debug:'true',
					language_in: 'ECMASCRIPT5',
					formatting: 'PRETTY_PRINT',
				}
			},
			tutor_debug: { // build tutor interface bundle under more restrictive
				// conditions to expose problems
				closurePath: '<%= dir.closure_compiler %>',
				js: '<%= source.tutor %>',
				jsOutputFile: "<%= dir.output %>/g-<%= pkg.name %>-tutor.min.js",
				externs: "<%= dir.src %>/CTATExternals.js",
				maxBuffer: 600,
				options: {
					//compilation_level: 'SIMPLE', //'WHITESPACE_ONLY,ADVANCED
					warning_level: 'VERBOSE', //QUIET, DEFAULT, VERBOSE
					debug:'true',
					language_in: 'ECMASCRIPT5',
					formatting: 'PRETTY_PRINT',
				}
			},
			sidebar: {
				closurePath: '<%= dir.closure_compiler %>',
				js: '<%= source.sidebar %>',
				jsOutputFile: "<%= dir.output %>/g-<%= pkg.name %>-tutor-sidebar.min.js",
				maxBuffer: 600,
				options: {
					//compilation_level: 'SIMPLE',
					language_in: 'ECMASCRIPT5',
					//formatting: 'PRETTY_PRINT',
				}
			},
			tracer: {
				closurePath: '<%= dir.closure_compiler %>',
				js: '<%= source.tracer %>',
				jsOutputFile: "<%= dir.output %>/g-<%= pkg.name %>-tracer.min.js",
				maxBuffer: 500,
				options: {
					//compilation_level: 'SIMPLE',
					language_in: 'ECMASCRIPT5',
					//formatting: 'PRETTY_PRINT',
				}
			},
		},
		jshint: { // basic source code checker
			tutor: '<%= source.tutor %>',
			sidebar: '<%= source.sidebar %>',
			tracer: '<%= source.tracer %>'
		},
		qunit: { // Unit tests
			CTATMath: ['test/CTATMath.html']
		},
		shell: {
			make_dep: { // generate the dependency file needed by goog.require to find CTAT files
				command: function() {
					var cmd = ["python"];
					cmd.push('<%= cmd.depswriter %>');
					cmd.push('--root_with_prefix="src ../../../../../src"'); // relative to closure-lib
					cmd.push('--output_file=<%= dir.dep %>');
					return cmd.join(' ');
				}
			},
			build_compiler: {
				command: "ant jar",
				options: {
					execOptions: {
						cwd: 'third-party/google/closure-compiler'
							// apparently needs to be hard coded as templates in cwd are not expanded.
					}
				}
			},
			clean_compiler: {
				command: "ant clean",
				options: {
					execOptions: {
						cwd: 'third-party/google/closure-compiler'
							// apparently needs to be hard coded as templates in cwd are not expanded.
					}
				}
			}

		},
		coffee: {
			files: {
				expand: true,
				cwd: '<%= dir.src %>/',
				src: ['**/*.coffee'],
				dest: 'src/',
				ext: '.js'
			}
		},
		jison: {
			files: {
				expand: true,
				cwd: '<%= dir.src %>/',
				src: ['**/*.jison'],
				dest: 'src/',
				ext: '.js'
			}
		},
		clean: {
			build: '<%= dir.output %>/**',
			dep: '<%= dir.dep %>',
			dist: '<%= dir.deploy %>',
		},
		trimtrailingspaces: {
			options: { filter: 'isFile', encoding: 'utf8', failIfTrimmed: false},
			tutor: { src: '<%= source.tutor %>'},
			sidebar: { src: '<%= source.sidebar %>'},
			tracer: { src: '<%= source.tracer %>'},
			gruntfile: { src: 'Gruntfile.js'}
		},
		copy: {
			deploy: {
				files: [
				        {expand: true, src: ['css/*.css'], dest: '<%= dir.deploy %>/'},
				        {expand: true, src: ['<%= dir.output %>/**.js'], dest: '<%= dir.deploy %>/'},
				        {expand: true, src: ['jquery/*'], dest: '<%= dir.deploy %>/'},
				        {expand: true, src: ['*.html','*.png'], dest: '<%= dir.deploy %>/'}
				        ],
			}
		},
		watch: {
			tutor: {
				files: ['<%= source.tutor %>'],
				tasks: ['tutor']
			}
		}
	});

	// Load the plug-ins
	grunt.loadNpmTasks('grunt-closure-compiler');
	grunt.loadNpmTasks('grunt-contrib-clean');
	grunt.loadNpmTasks('grunt-contrib-coffee');
	grunt.loadNpmTasks('grunt-contrib-copy');
	grunt.loadNpmTasks('grunt-contrib-jshint');
	grunt.loadNpmTasks('grunt-contrib-qunit');
	grunt.loadNpmTasks('grunt-contrib-watch');
	grunt.loadNpmTasks('grunt-jison');
	grunt.loadNpmTasks('grunt-shell');
	grunt.loadNpmTasks('grunt-trimtrailingspaces');

	// Targets
	grunt.registerTask('tutor',['closure-compiler:tutor']);
	grunt.registerTask('sidebar',['closure-compiler:sidebar']);
	grunt.registerTask('tracer',['closure-compiler:tracer']);
	// default task.  Used when grunt is called with no target
	grunt.registerTask('default', ['tutor','sidebar','tracer']);
	grunt.registerTask('test', ['qunit']);
	grunt.registerTask('dep', ['shell:make_dep']);
	grunt.registerTask('deploy', ['default', 'copy:deploy']);

};
