import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ModelsettingVO, ModelsettingForm, ModelsettingQuery } from '@/api/system/modelsetting/types';

/**
 * 查询功能参数列表
 * @param query
 * @returns {*}
 */

export const listModelsetting = (query?: ModelsettingQuery): AxiosPromise<ModelsettingVO[]> => {
  return request({
    url: '/system/modelsetting/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询功能参数详细
 * @param id
 */
export const getModelsetting = (id: string | number): AxiosPromise<ModelsettingVO> => {
  return request({
    url: '/system/modelsetting/' + id,
    method: 'get'
  });
};

/**
 * 新增功能参数
 * @param data
 */
export const addModelsetting = (data: ModelsettingForm) => {
  return request({
    url: '/system/modelsetting',
    method: 'post',
    data: data
  });
};

/**
 * 修改功能参数
 * @param data
 */
export const updateModelsetting = (data: ModelsettingForm) => {
  return request({
    url: '/system/modelsetting',
    method: 'put',
    data: data
  });
};

/**
 * 删除功能参数
 * @param id
 */
export const delModelsetting = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/modelsetting/' + id,
    method: 'delete'
  });
};

/**
 * 检查模板
 * @param qaid
 */
export const checkTemplate = (qaid: number): AxiosPromise => {
  return request({
    url: `/system/modelsetting/checktemplate/${qaid}`,
    method: 'get'
  });
};
