/**
 * @fileoverview Defines an object which is used to register CTAT components.
 * All CTAT components should be registered with the general class name (eg)
 * "CTATButton" that is used in interface description messages.
 * @example CTAT.ComponentRegistry.addComponentType('CTATButton',CTATButton);
 *
 * @author $Author: mringenb $
 * @version $Revision: 21690 $
 */
goog.provide('CTAT.ComponentRegistry');

/**
 * The object that stors a registry of available components and maps
 * interface description message component type names to the constructor
 * of that type.  One of the main reasons for doing this is to protect these
 * mappings from renaming of the constructors as is possible when using various
 * compilers.
 * @example if(CTAT.ComponentRegistry.hasOwnProperty('CTATButton')) {
 *            var comp = new CTAT.ComponentRegistry['CTATButton'] (...); }
 */
CTAT.ComponentRegistry = {};

/**
 * Adds an interface description message component type to the registry.
 * @param {string} aName 	A CTAT component type.
 * @param {function} aConstructor 	The constructor used to make a component of aName type.
 */
CTAT.ComponentRegistry.addComponentType = function (aName,aConstructor) {
	if (typeof(aName) == 'string') {
		if (this.hasOwnProperty(aName)) {
			alert(aName+' is already a registered component');
		} else {
			// TODO: add check if instanceof(CTATCompBase)
			this[aName] = aConstructor;
		}
	}
};