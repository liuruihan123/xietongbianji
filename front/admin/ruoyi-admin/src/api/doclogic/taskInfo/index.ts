import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { TaskInfoVO, TaskInfoForm, TaskInfoQuery } from '@/api/doclogic/taskInfo/types';

/**
 * 查询逻辑任务列表
 * @param query
 * @returns {*}
 */

export const listTaskInfo = (query?: TaskInfoQuery): AxiosPromise<TaskInfoVO[]> => {
  return request({
    url: '/doclogic/taskInfo/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询逻辑任务详细
 * @param id
 */
export const getTaskInfo = (id: string | number): AxiosPromise<TaskInfoVO> => {
  return request({
    url: '/doclogic/taskInfo/' + id,
    method: 'get'
  });
};

/**
 * 新增逻辑任务
 * @param data
 */
export const addTaskInfo = (data: TaskInfoForm) => {
  return request({
    url: '/doclogic/taskInfo',
    method: 'post',
    data: data
  });
};

/**
 * 修改逻辑任务
 * @param data
 */
export const updateTaskInfo = (data: TaskInfoForm) => {
  return request({
    url: '/doclogic/taskInfo',
    method: 'put',
    data: data
  });
};

/**
 * 删除逻辑任务
 * @param id
 */
export const delTaskInfo = (id: string | number | Array<string | number>) => {
  return request({
    url: '/doclogic/taskInfo/' + id,
    method: 'delete'
  });
};
