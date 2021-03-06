/* Copyright (c) 2012-2014 Boundless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/edl-v10.html
 *
 * Contributors:
 * Gabriel Roldan (Boundless) - initial implementation
 */
package org.locationtech.geogig.api.plumbing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URL;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.locationtech.geogig.api.Platform;

/**
 *
 */
public class ResolveGeogigDirTest {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void test() throws Exception {

        File workingDir = tmpFolder.newFolder("fakeWorkingDir");
        File fakeRepo = new File(workingDir, ".geogig");
        fakeRepo.mkdirs();

        Platform platform = mock(Platform.class);
        when(platform.pwd()).thenReturn(workingDir);

        URL resolvedRepoDir = new ResolveGeogigDir(platform).call().get();
        assertEquals(fakeRepo.toURI().toURL(), resolvedRepoDir);

        workingDir = new File(new File(workingDir, "subdir1"), "subdir2");
        workingDir.mkdirs();
        when(platform.pwd()).thenReturn(workingDir);

        resolvedRepoDir = new ResolveGeogigDir(platform).call().get();
        assertEquals(fakeRepo.toURI().toURL(), resolvedRepoDir);

        when(platform.pwd()).thenReturn(tmpFolder.getRoot());
        assertFalse(new ResolveGeogigDir(platform).call().isPresent());

    }

}
