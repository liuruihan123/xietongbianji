import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { TaskFragmentVO, TaskFragmentForm, TaskFragmentQuery } from '@/api/doclogic/taskFragment/types';

/**
 * 查询文档片段列表
 * @param query
 * @returns {*}
 */

export const listTaskFragment = (query?: TaskFragmentQuery): AxiosPromise<TaskFragmentVO[]> => {
  return request({
    url: '/doclogic/taskFragment/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询文档片段详细
 * @param id
 */
export const getTaskFragment = (id: string | number): AxiosPromise<TaskFragmentVO> => {
  return request({
    url: '/doclogic/taskFragment/' + id,
    method: 'get'
  });
};

/**
 * 新增文档片段
 * @param data
 */
export const addTaskFragment = (data: TaskFragmentForm) => {
  return request({
    url: '/doclogic/taskFragment',
    method: 'post',
    data: data
  });
};

/**
 * 修改文档片段
 * @param data
 */
export const updateTaskFragment = (data: TaskFragmentForm) => {
  return request({
    url: '/doclogic/taskFragment',
    method: 'put',
    data: data
  });
};

/**
 * 删除文档片段
 * @param id
 */
export const delTaskFragment = (id: string | number | Array<string | number>) => {
  return request({
    url: '/doclogic/taskFragment/' + id,
    method: 'delete'
  });
};
