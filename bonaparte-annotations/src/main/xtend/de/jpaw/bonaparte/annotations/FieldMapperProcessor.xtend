package de.jpaw.bonaparte.annotations;

import org.eclipse.xtend.lib.macro.AbstractMethodProcessor
import org.eclipse.xtend.lib.macro.Active
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration
import org.eclipse.xtend.lib.macro.declaration.TypeReference
import de.jpaw.bonaparte.core.BonaPortable
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration

@Active(typeof(FieldMapperProcessor))
    annotation FieldMapper {
}

// maps all fields from src to dst. Lax check, fields are copied if the generated Java type matches.
class FieldMapperProcessor extends AbstractMethodProcessor {

    // the annotated class must have a void return type and two parameters which are simple classes, dst and src
    // all fields from src will be copied to dst, if their name and type matches
    override doTransform(MutableMethodDeclaration method, extension TransformationContext context) {
        val bon = BonaPortable.newTypeReference
        //checks
        // we allow for return types and more than 2 parameters. The first 2 are used by the implementations.
        if (method.parameters.size < 2) {   //  || method.returnType != primitiveVoid
            method.addError("annotated method must have at least 2 parameters")
            return
        }
        val dst = method.parameters.get(0)
        val src = method.parameters.get(1)

        if (!bon.isAssignableFrom(src.type) || !bon.isAssignableFrom(dst.type)) {
            method.addError("both parameters must be instances of classes implementing BonaPortable")
            return
        }

        val oldBody = method.body
        method.body = [ '''
            «buildMapping(dst.type, src.type, dst.simpleName, src.simpleName, true)»
            «oldBody»
        ''' ]
    }

    def private static boolean isInSrc(FieldDeclaration srcField, ClassDeclaration entity) {
        try {
            val mm = entity.findDeclaredMethod('''get«srcField.simpleName.toFirstUpper»''')
            if (mm === null || mm.isStatic) {
                if (entity.extendedClass !== null)   // if there is a superclass, try that
                    return srcField.isInSrc(entity.extendedClass.type as ClassDeclaration)
                return false  // «fieldName» not found in target class or is static, and no superclass exists
            }
            if (mm.returnType == srcField.type)
                return true
        } catch (Exception e) {
            System::out.println('''Exception «e» thrown for lookup of field «srcField.simpleName»''')
        }
        return false  // exists, but type differs, or got an exception
    }

        def static public CharSequence buildMapping(TypeReference dst, TypeReference src, String dstName, String srcName, boolean includeSuperClasses) {
        val dstClass = dst.type as ClassDeclaration
        val srcClass = src.type as ClassDeclaration
        val superClassMapping = if (includeSuperClasses) dstClass.extendedClass?.buildMapping(src, dstName, srcName, includeSuperClasses)
        return '''
            «superClassMapping»«dstClass.declaredFields.filter[!isStatic && isInSrc(srcClass)].map['''«dstName».set«simpleName.toFirstUpper»(«srcName».get«simpleName.toFirstUpper»());'''].join('\n')»
        '''
    }
}
