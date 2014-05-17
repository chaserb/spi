/*
 * Copyright 2010 Chase Barrett. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. 
 */
package com.spi.fafs;

import org.w3c.dom.Node;

/**
 * Handler which knows how to apply a field in the HTML to an FAFSOrder.
 * 
 * @author Chase Barrett
 */
public interface InputFieldHandler
{
   public void applyInputField(String id, Node inputField, FAFSOrder order);
}
