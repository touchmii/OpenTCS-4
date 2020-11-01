/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.strategies.basic.routing.jgrapht;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.ALTAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.data.model.Point;
import org.opentcs.strategies.basic.routing.PointRouter;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Creates {@link PointRouter} instances based on the Dijkstra algorithm.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class AStarPointRouterFactory
    extends AbstractPointRouterFactory {

  /**
   * Creates a new instance.
   *
   * @param objectService The object service providing model data.
   * @param mapper Maps the plant model to a graph.
   */
  @Inject
  public AStarPointRouterFactory(@Nonnull TCSObjectService objectService,
                                 @Nonnull ModelGraphMapper mapper) {
    super(objectService, mapper);
  }

//  ALTAdmissibleHeuristic

  @Override
  protected ShortestPathAlgorithm<String, ModelEdge> createShortestPathAlgorithm(
      Graph<String, ModelEdge> graph, Collection<Point> points) {
//    graph.getAllEdges();
//    HashMap<String, Object> map = new HashMap<String, Object>();
//    Point pointA = new Point("A");
//    使用AStar算法除了需要传递一张地图（Graph）外还需要额外的启发式函数，也就是让寻路算法有一定的感知能力通过起点跟终端
//    判断出朝哪个反向可能比较进一下，其它反向就不考虑搜索，除非之前预判的哪个反向有障碍物阻挡。GraphT的这个AStar算法比较
//    完善，它要求的这个启发式函数做了什么事情呢，比如说天上的飞机，那些固定的航线就是提前规划好属于比较省油有安全的路线，而
//    从机场起飞到8000米高空的这段距离是飞行员自由发挥。如果要早一条飞国外的航线，首先就是从这些已经规划好的航线上面调，剩下
//    头尾的那点距离就自己规划。就像使用百度导航，国道跟高速公路那一段是不能改变的，至于其中的小路你可以随便开。
//    再说回这个函数，我们需要使用HashSet创建一个地标（landmark）集合，这个地标就相当于各条航线的首尾，或是高速公路的出入口。
//    根据这些地标先使用Dijkstra算法计算出每两个地标之间的最短路径，以后好方便AStar日后查询，而不用整段路径从头开始搜索了。
//    要发挥AStar的效率，就要挑选出合适的地标。这些地标跟地图模型是相关的，我们可以通过给点添加一个landmark属性，这样再编辑
//    地图的时候就确定好了地标，再把地图模型里面带有这个属性的点都给找出来传给这个启发式函数。
    Set<String> landmarks = new HashSet<String>();
//    map.put("1", pointA);
    //随意选取两个点，如果地图没有这两个点会报错
//    landmarks.add("1");
//    landmarks.add("2");
    for (Point point : points) {
//      this.points.put(point.getName(), point);
      if(point.getProperty("astar") != null) {
        landmarks.add(point.getName());
      }
    }
    return new AStarShortestPath<>(graph, new ALTAdmissibleHeuristic(graph, landmarks));
  }

}
