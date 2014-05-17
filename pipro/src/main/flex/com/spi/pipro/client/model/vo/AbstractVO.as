/*
* Copyright 2010 Chase Barrett. All rights reserved.
* PROPRIETARY/CONFIDENTIAL. 
*/
package com.spi.pipro.client.model.vo
{
   import flash.net.registerClassAlias;
   import flash.utils.ByteArray;
   import flash.utils.IDataInput;
   import flash.utils.IDataOutput;
   import flash.utils.IExternalizable;

   public class AbstractVO
   {
      public function AbstractVO()
      {
      }

      public function clone():AbstractVO
      {
         registerClassAlias("com.spi.pipro.client.model.vo.AbstractVO", AbstractVO);
         var bytes:ByteArray = new ByteArray();
         bytes.writeObject(this);
         bytes.position = 0;
         return bytes.readObject() as AbstractVO;
      }
   }
}