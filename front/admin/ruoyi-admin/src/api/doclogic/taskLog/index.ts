import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { TaskLogVO, TaskLogForm, TaskLogQuery } from '@/api/doclogic/taskLog/types';

/**
 * 查询分析日志列表
 * @param query
 * @returns {*}
 */

export const listTaskLog = (query?: TaskLogQuery): AxiosPromise<TaskLogVO[]> => {
  return request({
    url: '/doclogic/taskLog/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询分析日志详细
 * @param id
 */
export const getTaskLog = (id: string | number): AxiosPromise<TaskLogVO> => {
  return request({
    url: '/doclogic/taskLog/' + id,
    method: 'get'
  });
};

/**
 * 新增分析日志
 * @param data
 */
export const addTaskLog = (data: TaskLogForm) => {
  return request({
    url: '/doclogic/taskLog',
    method: 'post',
    data: data
  });
};

/**
 * 修改分析日志
 * @param data
 */
export const updateTaskLog = (data: TaskLogForm) => {
  return request({
    url: '/doclogic/taskLog',
    method: 'put',
    data: data
  });
};

/**
 * 删除分析日志
 * @param id
 */
export const delTaskLog = (id: string | number | Array<string | number>) => {
  return request({
    url: '/doclogic/taskLog/' + id,
    method: 'delete'
  });
};
