package com.spi.pipro.client.view
{
   import com.spi.pipro.client.view.component.HomePage;
   
   import mx.charts.events.ChartItemEvent;
   import mx.collections.ArrayCollection;
   import mx.controls.Alert;
   import mx.logging.ILogger;
   import mx.logging.Log;
   
   import org.robotlegs.mvcs.Mediator;
   
   public class HomePageMediator extends Mediator
   {
      /**
       * Storage for the logger instance.
       */
      private static const _log:ILogger = Log.getLogger("com.spi.pipro.client.view.HomePageMediator");
      
      public function HomePageMediator()
      {
         view.chart1.dataProvider = generateFirstModel();
         view.chart2.dataProvider = generateSecondModel();
      }
      
      public override function onRegister():void
      {
         view.chart1.addEventListener(ChartItemEvent.ITEM_CLICK, itemClickHandler);
         view.chart2.addEventListener(ChartItemEvent.ITEM_CLICK, itemClickHandler);
      }
      
      public override function onRemove():void
      {
         view.chart1.removeEventListener(ChartItemEvent.ITEM_CLICK, itemClickHandler);
         view.chart2.removeEventListener(ChartItemEvent.ITEM_CLICK, itemClickHandler);
         setViewComponent(null);
      }
      
      /**
       * Retrieve the view component, cast as the pipro app.
       */
      private function get view():HomePage
      {
         return viewComponent as HomePage;
      }
      
      private function itemClickHandler(e:ChartItemEvent):void
      {
         var values:Array = e.hitData.displayText.split("\n");
         var text:String = "This will take you to a page with the " + values[1] + 
            " orders that are \"" + values[0] + "\"";
         Alert.show(text, "Item Clicked");
      }
      
      private function generateFirstModel():ArrayCollection
      {
         return new ArrayCollection( [
            { DaysOut: "Overdue", NumOrders: 3},
            { DaysOut: "3 Days or Less", NumOrders: 32},
            { DaysOut: "6 Days or Less", NumOrders: 27},
            { DaysOut: "9 Days or Less", NumOrders: 25},
            { DaysOut: "12 Days or Less", NumOrders: 40}]);
      }

      private function generateSecondModel():ArrayCollection
      {
         return new ArrayCollection( [
            { Type: "New", NumOrders: 50},
            { Type: "Dispatched", NumOrders: 120},
            { Type: "Completed", NumOrders: 27},
            { Type: "Submitted", NumOrders: 300},
            { Type: "Cancelled", NumOrders: 6}]);
      }
   }
}