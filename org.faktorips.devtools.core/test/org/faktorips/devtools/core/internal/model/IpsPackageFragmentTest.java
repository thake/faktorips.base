package org.faktorips.devtools.core.internal.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.faktorips.devtools.core.IpsPluginTest;
import org.faktorips.devtools.core.model.IIpsElement;
import org.faktorips.devtools.core.model.IIpsObject;
import org.faktorips.devtools.core.model.IIpsObjectPath;
import org.faktorips.devtools.core.model.IpsObjectType;
import org.faktorips.devtools.core.model.IIpsPackageFragment;
import org.faktorips.devtools.core.model.IIpsPackageFragmentRoot;
import org.faktorips.devtools.core.model.IIpsProject;
import org.faktorips.devtools.core.model.IIpsSrcFile;
import org.faktorips.util.StringUtil;


/**
 *
 */
public class IpsPackageFragmentTest extends IpsPluginTest {
    
    private IIpsProject ipsProject;
    private IIpsPackageFragmentRoot rootPackage;
    private IpsPackageFragment pack;

    /*
     * @see PluginTest#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        ipsProject = this.newIpsProject("TestProject");
        rootPackage = ipsProject.getIpsPackageFragmentRoots()[0];
        pack = (IpsPackageFragment)rootPackage.createPackageFragment("products.folder", true, null);
    }
    
    public void testGetJavaPackageFragment() throws CoreException {
        IIpsObjectPath path = ipsProject.getIpsObjectPath();
        path.setOutputFolderForGeneratedJavaFiles(ipsProject.getProject().getFolder("generated"));
        path.setBasePackageNameForGeneratedJavaClasses("org.faktorips");
        path.setOutputDefinedPerSrcFolder(false);
        ipsProject.setIpsObjectPath(path);
        
        IPackageFragment javaPck= pack.getJavaPackageFragment(IIpsPackageFragment.JAVA_PACK_IMPLEMENTATION);
        String expectedName = "org.faktorips.internal." + pack.getName();
        assertEquals(expectedName, javaPck.getElementName());
        javaPck= pack.getJavaPackageFragment(IIpsPackageFragment.JAVA_PACK_PUBLISHED_INTERFACE);
        expectedName = "org.faktorips." + pack.getName();
        assertEquals(expectedName, javaPck.getElementName());

        // test with base package = ""
        path.setBasePackageNameForGeneratedJavaClasses("");
        ipsProject.setIpsObjectPath(path);
        javaPck= pack.getJavaPackageFragment(IIpsPackageFragment.JAVA_PACK_IMPLEMENTATION);
        expectedName = "internal." + pack.getName();
        assertEquals(expectedName, javaPck.getElementName());
        javaPck= pack.getJavaPackageFragment(IIpsPackageFragment.JAVA_PACK_PUBLISHED_INTERFACE);
        assertEquals(pack.getName(), javaPck.getElementName());

        // illegal kind
        try {
            javaPck= pack.getJavaPackageFragment(-1);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }
    
    public void testGetRelativePath(){
        
        String[] expectedSegments = pack.getName().split("\\.");
        String[] segments = pack.getRelativePath().segments();
        for (int i = 0; i < segments.length; i++) {
            assertEquals(expectedSegments[i],segments[i]);
        }
    }
    
    public void testGetAllJavaPackageFragments() throws CoreException {
        IPackageFragment[] javaPacks = pack.getAllJavaPackageFragments();
        assertEquals(3, javaPacks.length);
    }
    
    
    public void testGetElementName() {
        assertEquals("products.folder", pack.getName());
    }

    public void testGetPdRootFolder() {
        assertEquals(rootPackage, pack.getRoot());
    }

    public void testGetCorrespondingResource() {
        IResource resource = pack.getCorrespondingResource();
        assertTrue(resource instanceof IFolder);
        assertEquals("folder", resource.getName());
        assertTrue(resource.exists());
        IResource parent = resource.getParent();
        assertTrue(parent.exists());
        assertEquals("products", parent.getName());
        
        // default folder
        IIpsPackageFragment defaultFolder = rootPackage.getIpsPackageFragment("");
        assertEquals(rootPackage.getCorrespondingResource(), defaultFolder.getCorrespondingResource());
    }

    public void testExists() throws CoreException {
        assertTrue(pack.exists());
        // parent exists, but not the corresponding folder
        IIpsPackageFragment folder = rootPackage.getIpsPackageFragment("unkownFolder");
        assertFalse(folder.exists());
        // corresponding folder exists but not the parent (because root2
        // is not on the classpath
        IIpsPackageFragmentRoot root2 = ipsProject.getIpsPackageFragmentRoot("notonpath");
        ((IFolder)root2.getCorrespondingResource()).create(true, true, null);
        IIpsPackageFragment pck2 = root2.getIpsPackageFragment("pck2");
        ((IFolder)pck2.getCorrespondingResource()).create(true, true, null);
        assertFalse(pck2.exists());
    }
    
    public void testGetChildren() throws CoreException {
        assertEquals(0, pack.getChildren().length);
        
        pack.createIpsFile(IpsObjectType.POLICY_CMPT_TYPE, "MotorProduct", true, null);
        IIpsElement[] children = pack.getChildren();
        assertEquals(1, children.length);
        String filename = IpsObjectType.POLICY_CMPT_TYPE.getFileName("MotorProduct");
        assertEquals(pack.getIpsSrcFile(filename), children[0]);
        
        // folders should be ignored
        IFolder folder = (IFolder)pack.getCorrespondingResource();
        IFolder subfolder = folder.getFolder("subfolder");
        subfolder.create(true, true, null);
        assertEquals(1, pack.getChildren().length);
        
        // files with unkown file extentions should be ignored
        IFile newFile = folder.getFile("Blabla.unkownExtension");
        ByteArrayInputStream is = new ByteArrayInputStream("Contents".getBytes());
        newFile.create(is, true, null);
        assertEquals(1, pack.getChildren().length);
    }

    public void testGetPdSrcFile() {
        IIpsSrcFile file = pack.getIpsSrcFile("file");
        assertEquals("file", file.getName());
        assertEquals(pack, file.getParent());
    }

    /*
     * Class under test for IpsSrcFile createPdFile(String, String, boolean, IProgressMonitor)
     */
    public void testCreatePdFileStringStringbooleanIProgressMonitor() throws CoreException, IOException {
        IIpsSrcFile file = pack.createIpsFile("file", "blabla", true, null);
        assertTrue(file.exists());
        InputStream is = file.getCorrespondingFile().getContents();
        String contents = StringUtil.readFromInputStream(is, StringUtil.CHARSET_ISO_8859_1);
        assertEquals("blabla", contents);
    }
    
    public void testFindIpsObjectsStartingWith() throws CoreException {
        IIpsObject obj1 = newIpsObject(pack, IpsObjectType.POLICY_CMPT_TYPE, "MotorPolicy");
        IIpsObject obj2 = newIpsObject(pack, IpsObjectType.POLICY_CMPT_TYPE, "motorCoverage");
        
        ArrayList result = new ArrayList();
        
        // case sensitive
        pack.findIpsObjectsStartingWith(IpsObjectType.POLICY_CMPT_TYPE, "Motor", false, result);
        assertEquals(1, result.size());
        assertTrue(result.contains(obj1));

        // ignore case
        result.clear();
        pack.findIpsObjectsStartingWith(IpsObjectType.POLICY_CMPT_TYPE, "Motor", true, result);
        assertEquals(2, result.size());
        assertTrue(result.contains(obj1));
        assertTrue(result.contains(obj2));
        
        // nothing found because no policy component type exists starting with z
        result.clear();
        pack.findIpsObjectsStartingWith(IpsObjectType.POLICY_CMPT_TYPE, "Z", true, result);
        assertEquals(0, result.size());
        
        // nothing found, because no product component exists
        result.clear();
        pack.findIpsObjectsStartingWith(IpsObjectType.PRODUCT_CMPT, "M", true, result);
        assertEquals(0, result.size());
        
        // pack does not exists
        IpsPackageFragment pack2 = (IpsPackageFragment)rootPackage.getIpsPackageFragment("notExistingPack");
        pack2.findIpsObjectsStartingWith(IpsObjectType.POLICY_CMPT_TYPE, "Motor", true, result);
        assertEquals(0, result.size());
        

        // ipsobjecttype null
        try {
            pack.findIpsObjectsStartingWith(null, "M", true, result);
            fail();
        } catch (NullPointerException e) {
        }
        
        // prefix null
        try {
            pack.findIpsObjectsStartingWith(IpsObjectType.POLICY_CMPT_TYPE, null, true, result);
            fail();
        } catch (NullPointerException e) {
        }
        
        // result null
        try {
            pack.findIpsObjectsStartingWith(IpsObjectType.POLICY_CMPT_TYPE, "M", true, null);
            fail();
        } catch (NullPointerException e) {
        }
    }

    public void testGetIpsParentPackageFragment() {
    	// test for default-package
    	assertNull(this.pack.getIpsParentPackageFragment());
//    	assertEquals(this.pack.getIpsParentPackageFragment().getName(), "products");
    }
    
    public void testGetIpsChildPackageFragments() throws CoreException {
    	this.rootPackage.createPackageFragment("products.test1", true, null);
    	this.rootPackage.createPackageFragment("products.test2", true, null);
    	
    	IIpsPackageFragment[] children = this.rootPackage.getIpsDefaultPackageFragment().getIpsChildPackageFragments();
    	assertEquals(children.length, 1);
    	children = children[0].getIpsChildPackageFragments();
    	assertEquals(children.length, 3);
    	assertEquals(children[0].getName(), "products.folder");
    	assertEquals(children[1].getName(), "products.test1");
    	assertEquals(children[2].getName(), "products.test2");
    	
    }
    
}
